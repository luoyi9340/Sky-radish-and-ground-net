package org.sentry.group.com.channel_callback;

import org.sentry.nio.channel.IChannelCallback;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;


/**
 * channel状态变更相关回调
 * 
 * @author luoyi
 *
 */
public class ChannelCallbackImpl implements IChannelCallback {

	
	@Override
	public boolean beforeChannelInit(SocketChannel socketChannel, ChannelGroup channelGroup) {
		return true;
	}

	@Override
	public boolean onChannelException(Throwable t, ChannelHandlerContext ctx, ChannelGroup channelGroup) {
		return true;
	}

	@Override
	public void onChannelAdded(ChannelHandlerContext ctx, ChannelGroup channelGroup) {
		
	}

	@Override
	public void onChannelRemoved(ChannelHandlerContext ctx, ChannelGroup channelGroup) {
		
	}

	@Override
	public void onChannelReadIdle(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void onChannelWriteIdle(ChannelHandlerContext ctx) {
		
	}

	@Override
	public void onChannelIdle(ChannelHandlerContext ctx) {
		
	}

}
