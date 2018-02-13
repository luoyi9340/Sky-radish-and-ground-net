package org.sentry.group.com.msg_callback.client.read;

import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.com.msg.PongMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 收到pong回复消息callback
 * 
 * @author luoyi
 *
 */
public class PongReadCallback extends AClientMsgCallback<PongMsg> implements IMsgReadCallback<PongMsg> {

	
	public PongReadCallback() {super();}
	public PongReadCallback(IWaitReceiptQueue queue, CommunicationList.Info info, ComConnectHolder holder) {super(queue, info, holder);}
	
	
	@Override
	public void callbackHandly(ChannelHandlerContext ctx, PongMsg msg) {
		//	设置连接状态位有效
		holder.setConnect(true);
		
		//	ping消息发出时是有时效性的，这里收到回调后删除等待超时队列
		PingMsg ping = new PingMsg();
		ping.setSeqId(msg.getSeqId());
		waitReceiptQueue.remove(ping);
	}
	
}
