package org.sentry.nio.handly.callback;

import org.sentry.nio.msg.json.IJsonMsg;

import io.netty.channel.ChannelHandlerContext;


/**
 * 消息回调
 * 
 * @author luoyi
 *
 * @param <T>
 */
public interface IMsgCallback<T extends IJsonMsg> {
	
	
	/**
	 * 处理回调消息
	 * @param	ctx		netty channel环境
	 * @param	msg		消息
	 */
	void callback(ChannelHandlerContext ctx, T msg);
}
