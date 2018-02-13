package org.sentry.nio.msg;

import io.netty.channel.ChannelPipeline;

/**
 * 消息编解码器
 * 
 * @author luoyi
 *
 */
public interface IMsgCoderAppender {

	
	/**
	 * 追加消息编解码
	 */
	void appendCoder(ChannelPipeline pipeline);
	
}
