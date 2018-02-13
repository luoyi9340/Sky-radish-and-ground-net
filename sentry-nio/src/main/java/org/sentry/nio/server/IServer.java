package org.sentry.nio.server;

import org.sentry.nio.msg.IMsg;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

/**
 * 服务端接口
 * 
 * @author luoyi
 *
 */
public interface IServer {

	
	/**
	 * 开启服务
	 */
	void start(IStartCallback callback);
	
	
	/**
	 * 关闭服务
	 */
	void close(ICloseCallback callback);
	
	
	
	/**
	 * 是否在跑
	 */
	boolean isRunning();
	
	
	/**
	 * 取channelGroup
	 */
	ChannelGroup channelGroup();
	
	
	/**
	 * 发消息
	 */
	void write(Channel channel, IMsg msg);
	/**
	 * 发消息
	 */
	void writeAndFlush(Channel channel, IMsg msg);
	
	
	/**
	 * 服务器相关回调
	 */
	public static interface IStartCallback {
		//	服务器开启成功回调
		void onSucc();
		//	开启失败回调
		void onError(Throwable t);
	}
	public static interface ICloseCallback {
		//	服务器前关闭回调
		void onSucc();
		//	服务器关闭后回调
		void onError(Throwable t);
	}
	
}
