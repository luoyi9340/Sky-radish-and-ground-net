package org.sentry.group.com.msg_callback.server.read;

import org.sentry.group.com.ICom.IMsgReceivedCallback;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.com.msg.PongMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 收到pong回复消息callback
 * 
 * @author luoyi
 *
 */
public class PongReadCallback extends AServerMsgCallback<PongMsg> implements IMsgReadCallback<PongMsg> {

	
	public PongReadCallback() {super();}
	public PongReadCallback(IWaitReceiptQueue queue) {super(queue);}
	
	
	@Override
	public void callbackHandly(ChannelHandlerContext ctx, PongMsg msg) {
		//	ping消息发出时是有时效性的，这里收到回调后删除等待超时队列
		PingMsg ping = new PingMsg();
		ping.setSeqId(msg.getSeqId());
		
		//	尝试执行received回调
		IMsgReceivedCallback receivedCallback = waitReceiptQueue.getReceivedCallback(msg);
		if (receivedCallback != null) {receivedCallback.received(msg.getInfo(), msg);}
		
		//	删除等待回调
		waitReceiptQueue.remove(ping);
	}
	
}
