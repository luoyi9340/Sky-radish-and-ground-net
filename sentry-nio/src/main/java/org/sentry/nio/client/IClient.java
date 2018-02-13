package org.sentry.nio.client;

import org.sentry.nio.msg.IMsg;

import io.netty.channel.Channel;

/**
 * 客户端
 * 
 * @author luoyi
 *
 */
public interface IClient {

	
	/**
	 * 建立连接
	 */
	void connect(IConnectCallback callback);
	/**
	 * 建立连接
	 */
	void connect(String host, int port, IConnectCallback callback);
	/**
	 * 断开连接
	 */
	void disconnect(ICloseCallback callback);

	
	/**
	 * 挂载断开连接回调
	 */
	void addDisconnectCallback(IDisconnectCallback callback);
	
	
	/**
	 * 判断连接是否有效
	 */
	boolean isConnect();
	
	
	/**
	 * 写消息
	 */
	void write(IMsg msg);
	/**
	 * 立即写
	 */
	void writeAndFlush(IMsg msg);
	
	
	/**
	 * 连接相关回调
	 */
	public static interface IConnectCallback {
		/**
		 * 连接成功
		 */
		void onConnectSucc(Channel channel);
		/**
		 * 连接失败
		 * @param t
		 */
		void onConnectError(Throwable t);
	}
	/**
	 * 关闭相关回调
	 */
	public static interface ICloseCallback {
		/**
		 * 连接成功
		 */
		void onCloseSucc();
		/**
		 * 连接失败
		 * @param t
		 */
		void onCloseError(Throwable t);
	}
	/**
	 * 客户端断掉回调
	 */
	public static interface IDisconnectCallback {
		/**
		 * 断连回调
		 */
		void onDisconnect(String host, int port);
	}
	
}
