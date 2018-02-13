package org.sentry.nio.handly;

import io.netty.channel.ChannelPipeline;


/**
 * handler加载器
 * 
 * @author luoyi
 *
 */
public interface IHandlerAppender {

	
	/**
	 * 追加消息编解码
	 */
	void appendHandler(ChannelPipeline pipeline);
	
	
}
