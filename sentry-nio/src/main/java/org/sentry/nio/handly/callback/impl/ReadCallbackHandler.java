package org.sentry.nio.handly.callback.impl;

import org.apache.log4j.Logger;
import org.sentry.nio.handly.callback.ICallbackMapper;
import org.sentry.nio.handly.callback.IMsgReadCallback;
import org.sentry.nio.msg.json.IJsonMsg;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 读到消息时执行的回调
 * 
 * @author luoyi
 *
 */
@Sharable
public class ReadCallbackHandler extends SimpleChannelInboundHandler<IJsonMsg> {

	
	static Logger log = Logger.getLogger("sentry-nio-read-handler");
	
	
	//	消息各种mapper
	protected ICallbackMapper mapper;
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, IJsonMsg msg) throws Exception {
		//	从消息类型拿消息callback
		IMsgReadCallback callback = mapper.mapperRead(msg);
		if (callback == null) {
			log.warn("[JsonReadCallbackHandler] channelRead0 unknow msg callback. msg.type:" + msg.getClass().getSimpleName());
			
			throw new Exception("unknow msg callback. msg.type:" + msg.getClass().getSimpleName());
		}
		
		try {
			//	从context中取各种参数
			
			callback.callback(ctx, msg);
		}catch (Throwable t) {
			log.error("[JsonReadCallbackHandler] channelRead0 callback.callback error. " + t.getMessage(), t);
		}
	}


	public ICallbackMapper getMapper() {
		return mapper;
	}

	public void setMapper(ICallbackMapper mapper) {
		this.mapper = mapper;
	}
}
