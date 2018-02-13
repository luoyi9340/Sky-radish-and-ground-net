package org.sentry.group.com.msg_callback.server;

import org.apache.log4j.Logger;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.MsgTypeEnums;
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
public abstract class AServerMsgCallback<T extends IJsonMsg> implements IMsgCallback<T> {

	
	protected static Logger log = Logger.getLogger("sentry-group-com");
	
	
	//	消息超时队列
	protected IWaitReceiptQueue waitReceiptQueue;
	
	
	public AServerMsgCallback() {
		super();
	}
	public AServerMsgCallback(IWaitReceiptQueue waitReceiptQueue) {
		super();
		this.waitReceiptQueue = waitReceiptQueue;
	}
	

	@Override
	public void callback(ChannelHandlerContext ctx, T msg) {
		//	如果是ping/pong消息则不打
		if (msg.type() != MsgTypeEnums.Ping.getVal()
				&& msg.type() != MsgTypeEnums.Pong.getVal()) {
			if (this instanceof IMsgReadCallback) {
				log.info("[AMsgCallback] received msg. " + " msg:" + msg);
			}else {
				log.info("[AMsgCallback] send msg. msg:" + msg);
			}
		}
		
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
