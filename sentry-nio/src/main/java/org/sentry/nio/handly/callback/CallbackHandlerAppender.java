package org.sentry.nio.handly.callback;

import org.sentry.nio.handly.IHandlerAppender;
import org.sentry.nio.handly.callback.impl.ReadCallbackHandler;
import org.sentry.nio.handly.callback.impl.WriteCallbackHandler;

import io.netty.channel.ChannelPipeline;


/**
 * 回调机制的消息处理appender
 * 
 * @author luoyi
 *
 */
public class CallbackHandlerAppender implements IHandlerAppender {

	
	//	读写消息callback
	protected ReadCallbackHandler readCallbackHandler;
	protected WriteCallbackHandler writeCallbackHandler;
	
	
	@Override
	public void appendHandler(ChannelPipeline pipeline) {
		//	每个channel分配到一组独立的处理对象
		
		//	追加json消息编解码
		pipeline.addLast("handler-callback-read-" + pipeline.channel().id(), readCallbackHandler);
		pipeline.addLast("handler-callback-write-" + pipeline.channel().id(), writeCallbackHandler);
	}


	public ReadCallbackHandler getReadCallbackHandler() {
		return readCallbackHandler;
	}

	public void setReadCallbackHandler(ReadCallbackHandler readCallbackHandler) {
		this.readCallbackHandler = readCallbackHandler;
	}

	public WriteCallbackHandler getWriteCallbackHandler() {
		return writeCallbackHandler;
	}

	public void setWriteCallbackHandler(WriteCallbackHandler writeCallbackHandler) {
		this.writeCallbackHandler = writeCallbackHandler;
	}

}
