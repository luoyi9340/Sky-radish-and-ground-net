package org.sentry.group.com.msg_callback.server;

import java.util.HashMap;
import java.util.Map;

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
import org.sentry.group.com.msg_callback.server.read.NewMemberReadCallback;
import org.sentry.group.com.msg_callback.server.read.PingReadCallback;
import org.sentry.group.com.msg_callback.server.read.PongReadCallback;
import org.sentry.group.com.msg_callback.server.read.RAskMasterReadCallback;
import org.sentry.group.com.msg_callback.server.read.RAskMasterRespReadCallback;
import org.sentry.group.com.msg_callback.server.read.RConfirmResourceReadCallback;
import org.sentry.group.com.msg_callback.server.read.RConfirmResourceRespReadCallback;
import org.sentry.group.com.msg_callback.server.read.RElectedMasterReadCallback;
import org.sentry.group.com.msg_callback.server.read.RElectionMasterReadCallback;
import org.sentry.group.com.msg_callback.server.read.RElectionMasterRespReadCallback;
import org.sentry.group.com.msg_callback.server.read.RNoticeConfirmReadCallback;
import org.sentry.group.com.msg_callback.server.read.RNoticeConfirmRespReadCallback;
import org.sentry.group.com.msg_callback.server.read.RSignUpReadCallback;
import org.sentry.group.com.msg_callback.server.read.RSignUpRespReadCallback;
import org.sentry.group.com.msg_callback.server.write.GlobalMsgWriteCallback;
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
public class ServerCallbackMapperImpl implements ICallbackMapper {
	
	
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, IMsgReadCallback> readMapper = new HashMap<Class<?>, IMsgReadCallback>();
	@SuppressWarnings("rawtypes")
	protected Map<Class<?>, IMsgWriteCallback> writeMapper = new HashMap<Class<?>, IMsgWriteCallback>();
	
	
	//	消息超时队列
	protected IWaitReceiptQueue waitReceiptQueue;
	
	
	public ServerCallbackMapperImpl() {
		super();
	}


	public ServerCallbackMapperImpl(IWaitReceiptQueue waitReceiptQueue) {
		this.waitReceiptQueue = waitReceiptQueue;
		
		//	追加读callback
		//	ping/pong
		appendReadCallback(new PingReadCallback(waitReceiptQueue), PingMsg.class);
		appendReadCallback(new PongReadCallback(waitReceiptQueue), PongMsg.class);
		//	新成员消息
		appendReadCallback(new NewMemberReadCallback(), NewMemberMsg.class);
		//	决议报名
		appendReadCallback(new RSignUpReadCallback(waitReceiptQueue), RSignUpMsg.class);
		appendReadCallback(new RSignUpRespReadCallback(waitReceiptQueue), RSignUpRespMsg.class);
		//	寻主消息
		appendReadCallback(new RAskMasterReadCallback(waitReceiptQueue), RAskMasterMsg.class);
		appendReadCallback(new RAskMasterRespReadCallback(waitReceiptQueue), RAskMasterRespMsg.class);
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

}
