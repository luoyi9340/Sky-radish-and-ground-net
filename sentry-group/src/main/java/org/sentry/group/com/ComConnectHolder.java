package org.sentry.group.com;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ICom.IMsgTimeoutCallback;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.com.msg.MsgTypeMapperImpl;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.com.msg_callback.client.ClientCallbackMapperImpl;
import org.sentry.group.com.wait_receipt_queue.WaitReceiptQueueImpl;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.node.CommunicationList.Info;
import org.sentry.group.resolution.impl.confirm_resources.cofirm_master.ConfirmMasterResolution;
import org.sentry.group.node.RuleEnums;
import org.sentry.nio.channel.SentryNioChannelInitializer;
import org.sentry.nio.client.IClient;
import org.sentry.nio.client.IClient.IConnectCallback;
import org.sentry.nio.client.IClient.IDisconnectCallback;
import org.sentry.nio.client.impl.DefaultClientImpl;
import org.sentry.nio.handly.callback.CallbackHandlerAppender;
import org.sentry.nio.handly.callback.impl.ReadCallbackHandler;
import org.sentry.nio.handly.callback.impl.WriteCallbackHandler;
import org.sentry.nio.msg.json.JsonCoderAppender;

import io.netty.channel.Channel;

/**
 * 与其他人的连接保持
 * 
 * @author luoyi
 *
 */
public class ComConnectHolder extends Thread {
	
	
	static Logger log = Logger.getLogger("sentry-group-com");
	static Logger logGroup = Logger.getLogger("sentry-group");

	
	//	对方信息
	protected CommunicationList.Info info;
	//	连接信息
	protected IClient client;
	
	//	等待回执队列（某些特殊消息发出去后有时效性）
	protected IWaitReceiptQueue waitReceiptQueue;
	
	//	任何消息回调
	protected IMsgReceivedCallback receivedCallback;
	/**
	 * 追加任何消息回调
	 */
	public void appendReceivedCallback(IMsgReceivedCallback receivedCallback) {
		this.receivedCallback = receivedCallback;
	}
	
	
	//	连接是否有效
	protected volatile boolean connecting;
	//	是否活跃
	protected volatile boolean active;
	
	
	public ComConnectHolder(Info info) {
		super();
		this.info = info;
		
		WaitReceiptQueueImpl waitQueue = new WaitReceiptQueueImpl();
		waitQueue.setInfo(info);
		waitQueue.init();
		this.waitReceiptQueue = waitQueue;
		
		client = initClient(info);
	}
	
	
	@Override
	public void run() {
		active = true;
		
		while (active) {
			try {
				if (!connecting) {
					if (client != null) {
						client.disconnect(null);
						client = initClient(info);
					}else {
						client = initClient(info);
					}
				}
				
				//	如果没有连接则建连接
				if (!connecting || !client.isConnect()) {
					client.connect(info.getHost(), info.getPort(), new IConnectCallback() {
						public void onConnectSucc(Channel channel) {
							log.info("[ComConnectHolder] initClient finished. host:" + info.getHost() + " port:" + info.getPort());
							
							logGroup.info("[ComConnectHolder] 检测到节点连接. info:" + info);
							
							//	连接成功后想对方发送ping消息
							ping();
							connecting = true;
						}
						public void onConnectError(Throwable t) {
							connecting = false;
							if (disconnCallback != null) {
								disconnCallback.onDisconnect(info.getHost(), info.getPort());
							}
						}
					});
				}
				//	否则向对方发心跳（idle回调里做其实更好）
				else {
					ping();
				}
			}catch (Throwable t) {
				log.error("[ComConnectHolder] run error. " + t.getMessage(), t);
				t.printStackTrace();
			}
			
			
			synchronized (this) {
				try {
					//	如果连接是无效的，则1s后重连。否则5s后发心跳
					this.wait(GroupConfiguration.getPingInterval());
				} catch (InterruptedException e) {
				}
			}
		}
	}
	/**
	 * 向对方发送心跳
	 */
	public void ping() {
		//	连接成功后想对方发送ping消息
		PingMsg ping = new PingMsg();
		ping.setInfo(App.ME.self());
		ping.setSeqId(System.currentTimeMillis() + "");
		
		client.writeAndFlush(ping);
		
		waitReceiptQueue.append(ping, GroupConfiguration.getSocketTimeout(), new IMsgTimeoutCallback() {
			public void timeout(Info info, AGroupMsg msg) {
				log.info("[ComConnectHolder] ping timeout. info:" + info + " this:" + ComConnectHolder.this);
				
				//	已经无效的连接直接干掉
				ComConnectHolder.this.client.disconnect(null);
				ComConnectHolder.this.client = null;
				if (ComConnectHolder.this.disconnCallback != null) {
					ComConnectHolder.this.disconnCallback.onDisconnect(info.getHost(), info.getPort());
				}
			}
		});
	}
	
	
	//	节点挂掉的日志只打印一次
	protected boolean isLogNodeDone = true;
	//	连接断开回调
	protected IDisconnectCallback disconnCallback = new IDisconnectCallback() {
		public void onDisconnect(String host, int port) {
			//	只在首次断连时打印日志
			if (connecting) {
				log.warn("[ComConnectHolder] connect has disconnnect.  host:" + host + " port:" + port);
			}
			
			if (client != null) {
				client.disconnect(null);
				client = null;
			}
			
			if (!App.ME.getRule().equals(RuleEnums.Looking) && App.ME.getLeaderInfo() != null) {
				//	如果挂掉的不是leader，则不予理会
				if (!App.ME.getLeaderInfo().getInfo().equals(info)) {
					log.info("[ComConnectHolder] onDisconnect info:" + info + " not master.");
					//	如果是首次挂掉，打印日志
					if (connecting) {logGroup.info("[ComConnectHolder] 检测到节点挂掉，但挂掉的节点不是主节点，不予理会. info:" + info);}
				}
				//	如果挂掉的是主节点，则发起确认主节点是否有效决议
				else {
					log.info("[ComConnectHolder] onDisconnect info:" + info + " it's master. begin confirm master reslution.");
					logGroup.info("[ComConnectHolder] 检测到主节点挂掉，本节点退回到Loogin状态，并发起确认主节点是否有效决议. info:" + info);
					
					//	本节点退回Looking状态
					App.ME.setRule(RuleEnums.Looking);
					App.ME.setLeaderInfo(null);
					
					//	发起确认主是否可用决议
					ConfirmMasterResolution resolution = new ConfirmMasterResolution();
					resolution.setCommunicationList(App.ME.getCommunicationList());
					resolution.setCom(App.ME.getCom());
					resolution.setSignUpInterval(GroupConfiguration.getSentryGroupResolutionConfirmMasterSignupInterval());
					resolution.setVoteInterval(GroupConfiguration.getSentryGroupResolutionConfirmMasterVoteInterval());
					App.ME.getResolutionPool().start(resolution);
				}
			}
			
			connecting = false;
		}
	};
	protected IClient initClient(final CommunicationList.Info info) {
//		log.info("[ComConnectHolder] initClient host:" + info.getHost() + " port:" + info.getPort());
		final DefaultClientImpl dc = new DefaultClientImpl();
		
		//	channel初始化工具
		SentryNioChannelInitializer channelInitializer = new SentryNioChannelInitializer();
		//	消息编解码工具
		JsonCoderAppender msgCoderAppender = new JsonCoderAppender();
		msgCoderAppender.setMapper(new MsgTypeMapperImpl());
		channelInitializer.setMsgCoderAppender(msgCoderAppender);
		//	读写消息mapper注册，handler会调mapper
		CallbackHandlerAppender handlerAppender = new CallbackHandlerAppender();
		ClientCallbackMapperImpl mapper = new ClientCallbackMapperImpl(waitReceiptQueue, info, this);
		//	读消息handler注册
		ReadCallbackHandler readCallbackHandler = new ReadCallbackHandler();
		readCallbackHandler.setMapper(mapper);
		handlerAppender.setReadCallbackHandler(readCallbackHandler);
		//	写消息handler注册
		WriteCallbackHandler writeCallbackHandler = new WriteCallbackHandler();
		writeCallbackHandler.setMapper(mapper);
		handlerAppender.setWriteCallbackHandler(writeCallbackHandler);
		
		channelInitializer.setHandlerAppender(handlerAppender);
		
		dc.setChannelInitializer(channelInitializer);
		
		dc.addDisconnectCallback(disconnCallback);
		
		dc.init();
		
		return dc;
	}
	
	
	/**
	 * 发消息
	 */
	public boolean send(AGroupMsg msg) {
		if (!isConnect()) {return false;}
		
		client.writeAndFlush(msg);
		
		return true;
	}
	/**
	 * 发送带有时效的消息
	 * <p>	一定时间内没收到回执则判定消息超时
	 * <p>	回执的seqId与发出去的一致
	 */
	public boolean send(AGroupMsg msg, long timeout, IMsgTimeoutCallback callback) {
		if (!isConnect()) {return false;}
		
		client.writeAndFlush(msg);
		//	消息追加到等待超时队列
		waitReceiptQueue.append(msg, timeout, callback);
		
		return true;
	}
	/**
	 * 发送带有时效的消息
	 * <p>	一定时间内没收到回执则判定消息超时
	 * <p>	回执的seqId与发出去的一致
	 */
	public boolean send(AGroupMsg msg, long timeout, IMsgTimeoutCallback callback, ICom.IMsgReceivedCallback receivedCallback) {
		if (!isConnect()) {return false;}
		
		client.writeAndFlush(msg);
		//	消息追加到等待回执队列
		waitReceiptQueue.append(msg, timeout, callback, receivedCallback);
		
		return true;
	}
	
	
	
	/**
	 * 连接是否有效
	 */
	public boolean isConnect() {
		return connecting;
	}
	/**
	 * 设置连接状态
	 */
	public void setConnect(boolean bool) {
		this.connecting = bool;
	}


	public IClient getClient() {
		return client;
	}

	public void setClient(IClient client) {
		this.client = client;
	}

	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}
	
	
	/**
	 * 关闭
	 */
	public void close(IClient.ICloseCallback callback) {
		active = false;
		
		client.disconnect(callback);
	}
	
	
	/**
	 * 收到任何消息回调
	 */
	public static interface IMsgReceivedCallback {
		void received(AGroupMsg msg);
	}


	public IMsgReceivedCallback getReceivedCallback() {
		return receivedCallback;
	}


	public void setReceivedCallback(IMsgReceivedCallback receivedCallback) {
		this.receivedCallback = receivedCallback;
	}
}
