package org.sentry.nio.handly;

import java.util.List;

import org.netty.channel.SimpleChannelOutboundHandler;

import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 哨兵默认消息处理appender
 * 
 * 
 * @author luoyi
 *
 */
public class DefaultHandlerAppender implements IHandlerAppender {

	
	//	读/写消息handler列表
	protected List<SimpleChannelInboundHandler<?>> readHandlerList;
	protected List<SimpleChannelOutboundHandler<?>> writeHandlerList;
	
	
	@Override
	public void appendHandler(ChannelPipeline pipeline) {
		
		//	挂载写消息handler
		if (writeHandlerList != null) {
			for (SimpleChannelOutboundHandler<?> outbound : writeHandlerList) {
				pipeline.addLast(outbound);
			}
		}
		
		//	挂载读消息handler
		if (readHandlerList != null) {
			for (SimpleChannelInboundHandler<?> inbound : readHandlerList) {
				pipeline.addLast(inbound);
			}
		}
		
	}


	public List<SimpleChannelInboundHandler<?>> getReadHandlerList() {
		return readHandlerList;
	}


	public void setReadHandlerList(List<SimpleChannelInboundHandler<?>> readHandlerList) {
		this.readHandlerList = readHandlerList;
	}


	public List<SimpleChannelOutboundHandler<?>> getWriteHandlerList() {
		return writeHandlerList;
	}


	public void setWriteHandlerList(List<SimpleChannelOutboundHandler<?>> writeHandlerList) {
		this.writeHandlerList = writeHandlerList;
	}
	
}
