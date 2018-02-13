package org.sentry.nio.msg.json;

import org.sentry.nio.msg.IMsgCoderAppender;
import org.sentry.nio.msg.json.decoder.JsonDecoder;
import org.sentry.nio.msg.json.encoder.JsonEncoder;

import io.netty.channel.ChannelPipeline;


/**
 * json消息编解码器
 * 
 * @author luoyi
 *
 */
public class JsonCoderAppender implements IMsgCoderAppender {
	
	
	//	字符编码
	protected String charactor = "UTF-8";
	
	//	消息与type mapper
	protected IMsgTypeMapper mapper;
	
	
	public void appendCoder(ChannelPipeline pipeline) {
		//	每个channel分配到一组独立的处理对象
		
		//	追加json消息编解码
		pipeline.addLast("json-encoder-" + pipeline.channel().id(), new JsonEncoder(charactor));
		pipeline.addLast("json-decoder-" + pipeline.channel().id(), new JsonDecoder(charactor, mapper));
	}


	public String getCharactor() {
		return charactor;
	}

	public void setCharactor(String charactor) {
		this.charactor = charactor;
	}

	public IMsgTypeMapper getMapper() {
		return mapper;
	}

	public void setMapper(IMsgTypeMapper mapper) {
		this.mapper = mapper;
	}

}
