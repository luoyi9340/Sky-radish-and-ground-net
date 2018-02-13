package org.sentry.group.com.msg_callback.client;

import org.apache.log4j.Logger;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.MsgTypeEnums;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.handly.callback.IMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;
import org.sentry.nio.msg.json.IJsonMsg;

import io.netty.channel.ChannelHandlerContext;


/**
 * 消息回调统一父类
 * 
 * @author luoyi
 *
 * @param <T>
 */
public abstract class AClientMsgCallback<T extends IJsonMsg> implements IMsgCallback<T> {

	
	protected static Logger log = Logger.getLogger("sentry-group-com");
	
	
	//	消息超时队列
	protected IWaitReceiptQueue waitReceiptQueue;
	//	对方信息
	protected CommunicationList.Info info;
	//	对方连接信息
	protected ComConnectHolder holder;
	
	
	public AClientMsgCallback() {
		super();
	}
	public AClientMsgCallback(IWaitReceiptQueue waitReceiptQueue, CommunicationList.Info info, ComConnectHolder holder) {
		super();
		this.waitReceiptQueue = waitReceiptQueue;
		this.info = info;
		this.holder = holder;
	}
	

	@Override
	public void callback(ChannelHandlerContext ctx, T msg) {
		//	如果是ping/pong消息则不打
		if (msg.type() != MsgTypeEnums.Ping.getVal()
				&& msg.type() != MsgTypeEnums.Pong.getVal()) {
			if (this instanceof IMsgReadCallback) {
				log.info("[AMsgCallback] received msg. from " + (info == null ? "unknow" : info) + " msg:" + msg);
			}else {
				log.info("[AMsgCallback] send msg. msg:" + msg);
			}
		}
		
		//	之心消息收到回调
//		if (holder.getReceivedCallback() != null) {
//			holder.getReceivedCallback().received(msg);
//		}
		
		callbackHandly(ctx, msg);
	}
	
	
	protected abstract void callbackHandly(ChannelHandlerContext ctx, T msg);
	
	public IWaitReceiptQueue getWaitReceiptQueue() {
		return waitReceiptQueue;
	}

	public void setWaitReceiptQueue(IWaitReceiptQueue waitReceiptQueue) {
		this.waitReceiptQueue = waitReceiptQueue;
	}
}
