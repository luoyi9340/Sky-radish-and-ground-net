package org.sentry.group.com.msg_callback.client;

import java.util.HashMap;
import java.util.Map;

import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.NewMemberMsg;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.com.msg.PongMsg;
import org.sentry.group.com.msg.resolution.RAskMasterMsg;
import org.sentry.group.com.msg.resolution.RAskMasterRespMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceRespMsg;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterRespMsg;
import org.sentry.group.com.msg.resolution.RNoticeConfirmMsg;
import org.sentry.group.com.msg.resolution.RNoticeConfirmRespMsg;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.com.msg_callback.client.read.NewMemberReadCallback;
import org.sentry.group.com.msg_callback.client.read.PingReadCallback;
import org.sentry.group.com.msg_callback.client.read.PongReadCallback;
import org.sentry.group.com.msg_callback.client.read.RAskMasterReadCallback;
import org.sentry.group.com.msg_callback.client.read.RAskMasterRespReadCallback;
import org.sentry.group.com.msg_callback.client.read.RConfirmResourceReadCallback;
import org.sentry.group.com.msg_callback.client.read.RConfirmResourceRespReadCallback;
import org.sentry.group.com.msg_callback.client.read.RElectedMasterReadCallback;
import org.sentry.group.com.msg_callback.client.read.RElectionMasterReadCallback;
import org.sentry.group.com.msg_callback.client.read.RElectionMasterRespReadCallback;
import org.sentry.group.com.msg_callback.client.read.RNoticeConfirmReadCallback;
import org.sentry.group.com.msg_callback.client.read.RNoticeConfirmRespReadCallback;
import org.sentry.group.com.msg_callback.client.read.RSignUpReadCallback;
import org.sentry.group.com.msg_callback.client.read.RSignUpRespReadCallback;
import org.sentry.group.com.msg_callback.client.write.GlobalMsgWriteCallback;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.handly.callback.ICallbackMapper;
import org.sentry.nio.handly.callback.IMsgReadCallback;
import org.sentry.nio.handly.callback.IMsgWriteCallback;
import org.sentry.nio.msg.json.IJsonMsg;


/**
 * 各种回调 mapper
 * 
 * @author luoyi
 *
 */
public class ClientCallbackMapperImpl implements ICallbackMapper {
	
	
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, IMsgReadCallback> readMapper = new HashMap<Class<?>, IMsgReadCallback>();
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, IMsgWriteCallback> writeMapper = new HashMap<Class<?>, IMsgWriteCallback>();
	
	
	//	消息超时队列
	protected IWaitReceiptQueue waitReceiptQueue;
	//	对方信息
	protected CommunicationList.Info info;
	//	对方连接信息
	protected ComConnectHolder holder;
	
	
	public ClientCallbackMapperImpl() {
		super();
	}


	public ClientCallbackMapperImpl(IWaitReceiptQueue waitReceiptQueue, CommunicationList.Info info, ComConnectHolder holder) {
		this.waitReceiptQueue = waitReceiptQueue;
		this.info = info;
		this.holder = holder;
		
		//	追加读callback
		//	ping/pong
		appendReadCallback(new PingReadCallback(waitReceiptQueue, info, holder), PingMsg.class);
		appendReadCallback(new PongReadCallback(waitReceiptQueue, info, holder), PongMsg.class);
		//	新成员通知
		appendReadCallback(new NewMemberReadCallback(), NewMemberMsg.class);
		//	报名消息
		appendReadCallback(new RSignUpReadCallback(waitReceiptQueue, info, holder), RSignUpMsg.class);
		appendReadCallback(new RSignUpRespReadCallback(waitReceiptQueue, info, holder), RSignUpRespMsg.class);
		//	寻主消息
		appendReadCallback(new RAskMasterReadCallback(waitReceiptQueue, info, holder), RAskMasterMsg.class);
		appendReadCallback(new RAskMasterRespReadCallback(waitReceiptQueue, info, holder), RAskMasterRespMsg.class);
		//	主当选消息
		appendReadCallback(new RElectedMasterReadCallback(), RElectedMasterMsg.class);
		//	资源确认消息
		appendReadCallback(new RConfirmResourceReadCallback(), RConfirmResourceMsg.class);
		appendReadCallback(new RConfirmResourceRespReadCallback(), RConfirmResourceRespMsg.class);
		//	选主消息
		appendReadCallback(new RElectionMasterReadCallback(), RElectionMasterMsg.class);
		appendReadCallback(new RElectionMasterRespReadCallback(), RElectionMasterRespMsg.class);
		//	通知主开启某项决议消息
		appendReadCallback(new RNoticeConfirmReadCallback(), RNoticeConfirmMsg.class);
		appendReadCallback(new RNoticeConfirmRespReadCallback(), RNoticeConfirmRespMsg.class);
		
		//	追加写callback
		appendWriteCallback(new GlobalMsgWriteCallback(), IJsonMsg.class);
	}
	
	
	/**
	 * 追加读callback
	 */
	public void appendReadCallback(IMsgReadCallback<?> callback, Class<?> clazz) {
		readMapper.put(clazz, callback);
	}
	/**
	 * 追加写callback
	 */
	public void appendWriteCallback(IMsgWriteCallback<?> callback, Class<?> clazz) {
		writeMapper.put(clazz, callback);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IJsonMsg> IMsgReadCallback<T> mapperRead(IJsonMsg msg) {
		return readMapper.get(msg.getClass());
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IJsonMsg> IMsgWriteCallback<T> mapperWrite(IJsonMsg msg) {
		return writeMapper.get(msg.getClass());
	}


	public IWaitReceiptQueue getWaitReceiptQueue() {
		return waitReceiptQueue;
	}


	public void setWaitReceiptQueue(IWaitReceiptQueue waitReceiptQueue) {
		this.waitReceiptQueue = waitReceiptQueue;
	}


	public CommunicationList.Info getInfo() {
		return info;
	}


	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}


	public ComConnectHolder getHolder() {
		return holder;
	}


	public void setHolder(ComConnectHolder holder) {
		this.holder = holder;
	}

}
