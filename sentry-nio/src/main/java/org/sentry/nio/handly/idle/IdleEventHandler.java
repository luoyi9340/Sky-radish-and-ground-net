package org.sentry.nio.handly.idle;

import org.sentry.nio.channel.IChannelCallback;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;


/**
 * idle事件处理
 * 
 * @author luoyi
 *
 */
public class IdleEventHandler extends ChannelInboundHandlerAdapter {

	
	//	channel各种回调
	protected IChannelCallback channelCallback;
	
	
	/**
	 * 触发idle事件
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//	只处理Idle事件
		if (evt != null
				&& evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.READER_IDLE) {
				if (channelCallback != null) {channelCallback.onChannelReadIdle(ctx);}
			}
			else if (state == IdleState.WRITER_IDLE) {
				if (channelCallback != null) {channelCallback.onChannelWriteIdle(ctx);}
			}
			else {
				if (channelCallback != null) {channelCallback.onChannelIdle(ctx);}
			}
		}else {
			super.userEventTriggered(ctx, evt);
		}
	}

	
	public IChannelCallback getChannelCallback() {
		return channelCallback;
	}

	public void setChannelCallback(IChannelCallback channelCallback) {
		this.channelCallback = channelCallback;
	}
	
}
