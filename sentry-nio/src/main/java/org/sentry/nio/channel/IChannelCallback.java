package org.sentry.nio.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;

/**
 * channel相关callback
 * 
 * @author luoyi
 *
 */
public interface IChannelCallback {

	
	/**
	 * channel初始化
	 * <p>	如果方法返回false，则放弃初始化，关掉此channel
	 * 
	 * @param ch
	 * @return
	 */
	boolean beforeChannelInit(SocketChannel socketChannel, ChannelGroup channelGroup);
	
	
	/**
	 * channel出现异常
	 * <p>	如果方法中返回false，则直接断掉channel
	 */
	boolean onChannelException(Throwable t, ChannelHandlerContext ctx, ChannelGroup channelGroup);
	
	
	/**
	 * channel被追加到当前环境中
	 */
	void onChannelAdded(ChannelHandlerContext ctx, ChannelGroup channelGroup);
	/**
	 * channel被移除当前环境
	 */
	void onChannelRemoved(ChannelHandlerContext ctx, ChannelGroup channelGroup);
	
	
	/**
	 * channel关闭
	 */
	
	
	
	/**
	 * channel触发读idle
	 */
	void onChannelReadIdle(ChannelHandlerContext ctx);
	/**
	 * channel触发写idle
	 */
	void onChannelWriteIdle(ChannelHandlerContext ctx);
	/**
	 * channel触发idle
	 */
	void onChannelIdle(ChannelHandlerContext ctx);
}
