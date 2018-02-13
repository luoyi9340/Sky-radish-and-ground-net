package org.sentry.group.com;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.com.msg.MsgTypeMapperImpl;
import org.sentry.group.com.msg_callback.server.ServerCallbackMapperImpl;
import org.sentry.group.com.wait_receipt_queue.WaitReceiptQueueImpl;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.node.CommunicationList.Info;
import org.sentry.nio.channel.SentryNioChannelInitializer;
import org.sentry.nio.handly.callback.CallbackHandlerAppender;
import org.sentry.nio.handly.callback.impl.ReadCallbackHandler;
import org.sentry.nio.handly.callback.impl.WriteCallbackHandler;
import org.sentry.nio.msg.json.JsonCoderAppender;
import org.sentry.nio.server.IServer;
import org.sentry.nio.server.IServer.ICloseCallback;
import org.sentry.nio.server.IServer.IStartCallback;
import org.sentry.nio.server.impl.DefaultServerImpl;


/**
 * 通讯器默认实现
 * 
 * @author luoyi
 *
 */
public class ComImpl implements ICom {
	
	
	static Logger log = Logger.getLogger("sentry-group-com");
	static Logger logGroup = Logger.getLogger("sentry-group");
	static Logger logResolution = Logger.getLogger("sentry-resolution");

	
	//	
	protected IServer server;
	
	//	通讯清单中每个节点的连接
	protected Map<CommunicationList.Info, ComConnectHolder> clients = new HashMap<CommunicationList.Info, ComConnectHolder>();
	//	通讯清单
	protected CommunicationList communicationList;
	
	
	//	任何消息回调
	protected IMsgReceivedCallback receivedCallback;
	
	//	等待回执消息队列
	protected IWaitReceiptQueue waitReceiptQueue = null;
	
	
	public void init() {
		try {
			initServer();
		}catch (Throwable t) {
			log.error("[ICom] 初始化通讯器对外服务异常. " + t.getMessage(), t);
			logGroup.error("[ICom] 初始化通讯器对外服务异常. 节点无法对外提供服务，启动失败." + t.getMessage());
			
			if (server != null) {
				server.close(new ICloseCallback() {
					public void onSucc() {
						App.errorClose();
					}
					public void onError(Throwable t) {
					}
				});
			}
		}
	}


	/**
	 * 初始化server
	 */
	public void initServer() {
		log.info("[ICom] initServer ...");
		if (waitReceiptQueue == null) {
			WaitReceiptQueueImpl waitQueue = new WaitReceiptQueueImpl();
			waitQueue.init();
			waitReceiptQueue = waitQueue;
		}
		
		DefaultServerImpl ds = new DefaultServerImpl();
		ds.setBossGroupSize(1);
		ds.setWorkerGroupSize(2);
		ds.setPort(GroupConfiguration.getPort());
		
		//	channel初始化工具
		SentryNioChannelInitializer channelInitializer = new SentryNioChannelInitializer();
		//	消息编解码工具
		JsonCoderAppender msgCoderAppender = new JsonCoderAppender();
		msgCoderAppender.setMapper(new MsgTypeMapperImpl());
		channelInitializer.setMsgCoderAppender(msgCoderAppender);
		//	读写消息handler
		CallbackHandlerAppender handlerAppender = new CallbackHandlerAppender();
		ServerCallbackMapperImpl mapper = new ServerCallbackMapperImpl(waitReceiptQueue);

		ReadCallbackHandler readCallbackHandler = new ReadCallbackHandler();
		readCallbackHandler.setMapper(mapper);
		handlerAppender.setReadCallbackHandler(readCallbackHandler);
		
		WriteCallbackHandler writeCallbackHandler = new WriteCallbackHandler();
		writeCallbackHandler.setMapper(mapper);
		handlerAppender.setWriteCallbackHandler(writeCallbackHandler);
		
		channelInitializer.setHandlerAppender(handlerAppender);

		ds.setChannelInitializer(channelInitializer);

		ds.init();
		server = ds;
		
		server.start(new IStartCallback() {
			public void onSucc() {
				log.info("[ICom] initServer finished. port:" + GroupConfiguration.getPort());
				System.out.println("[ICom] initServer finished. port:" + GroupConfiguration.getPort());
			}
			public void onError(Throwable t) {
				log.error("[ICom] initServer error. " + t.getMessage(), t);
				
				if (server != null) {
					server.close(new ICloseCallback() {
						public void onSucc() {
							App.errorClose();
						}
						public void onError(Throwable t) {
						}
					});
				}
			}
		});
	}
	
	
	@Override
	public void appendReceivedCallback(IMsgReceivedCallback callback) {
		this.receivedCallback = callback;
	}
	@Override
	public void triggerReceivedCallback(Info info, AGroupMsg msg) {
		//	回调通讯器
		if (this.receivedCallback != null) {
			logResolution.info("[报名消息跟踪] 通讯器回调执行received消息. msg:" + msg + " info:" + info);
			
			this.receivedCallback.received(info, msg);
		}
	}
	
	
	@Override
	public void close(IServer.ICloseCallback callback) {
		//	释放服务端与所有客户端资源
		server.close(callback);
		
		//	释放所有活着的客户端
		for (ComConnectHolder holder : clients.values()) {
			holder.close(null);
		}
	}


	/**
	 * 根据通讯清单建立连接
	 */
	public void initTeamConnect(CommunicationList list) {
		for (final CommunicationList.Info info : list.allInfos()) {
			//	如果host和port都是自己则不予连接
			if (App.ME.isSelf(info)) {
				continue ;
			}
			
			ComConnectHolder holder = new ComConnectHolder(info);
			//	追加监听
			holder.appendReceivedCallback(new ComConnectHolder.IMsgReceivedCallback() {
				public void received(AGroupMsg msg) {
					//	回调通讯器
					if (ComImpl.this.receivedCallback != null) {
						ComImpl.this.receivedCallback.received(info, msg);
					}
				}
			});
			holder.start();
			
			clients.put(info, holder);
		}
	}
	@Override
	public ComConnectHolder getConnectHolder(Info info) {
		return clients.get(info);
	}
	@Override
	public boolean exists(Info info) {
		return communicationList.exists(info);
	}
	/**
	 * 
	 */
	@Override
	public void append(final Info info) {
		//	如果host和port都是自己则不予连接
		if (App.ME.isSelf(info)) {
			return ;
		}
		//	追加到通讯清单（清单自己会做去重操作）
		communicationList.append(info);
		
		ComConnectHolder holder = new ComConnectHolder(info);
		//	追加监听
		holder.appendReceivedCallback(new ComConnectHolder.IMsgReceivedCallback() {
			public void received(AGroupMsg msg) {
				//	回调通讯器
				if (ComImpl.this.receivedCallback != null) {
					ComImpl.this.receivedCallback.received(info, msg);
				}
			}
		});
		holder.start();
		
		clients.put(info, holder);
	}


	@Override
	public boolean send(Info info, AGroupMsg msg) {
		ComConnectHolder holder = clients.get(info);
		if (holder == null || !holder.isConnect()) {return false;}
		
		return holder.send(msg);
	}

	
	
	@Override
	public boolean send(Info info, AGroupMsg msg, long timeout, IMsgTimeoutCallback callback) {
		ComConnectHolder holder = clients.get(info);
		if (holder == null || !holder.isConnect()) {return false;}
		
		return holder.send(msg, timeout, callback);
	}


	public CommunicationList getCommunicationList() {
		return communicationList;
	}


	public void setCommunicationList(CommunicationList communicationList) {
		this.communicationList = communicationList;
	}

}
