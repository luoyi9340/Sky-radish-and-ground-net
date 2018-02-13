package org.sentry.nio.handly.callback.impl;

import org.apache.log4j.Logger;
import org.netty.channel.SimpleChannelOutboundHandler;
import org.sentry.nio.handly.callback.ICallbackMapper;
import org.sentry.nio.handly.callback.IMsgWriteCallback;
import org.sentry.nio.msg.json.IJsonMsg;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;


/**
 * 写入消息的回调
 * 
 * 
 * @author luoyi
 *
 */
@Sharable
public class WriteCallbackHandler extends SimpleChannelOutboundHandler<IJsonMsg> {

	
	static Logger log = Logger.getLogger("sentry-nio-write-handler");
	
	
	//	消息各种mapper
	protected ICallbackMapper mapper;
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void channelWrite0(ChannelHandlerContext ctx, IJsonMsg msg) throws Exception {
		//	从消息类型拿消息callback
		IMsgWriteCallback callback = mapper.mapperWrite(msg);
		if (callback == null) {
			log.warn("[JsonReadCallbackHandler] channelWrite0 unknow msg callback. msg.type:" + msg.getClass().getSimpleName());
			
//			throw new Exception("unknow msg callback. msg.type:" + msg.type());
			return;
		}
		
		try {
			callback.callback(ctx, msg);
		}catch (Throwable t) {
			log.error("[JsonReadCallbackHandler] channelWrite0 callback.callback error. " + t.getMessage(), t);
		}
	}


	public ICallbackMapper getMapper() {
		return mapper;
	}

	public void setMapper(ICallbackMapper mapper) {
		this.mapper = mapper;
	}
}
