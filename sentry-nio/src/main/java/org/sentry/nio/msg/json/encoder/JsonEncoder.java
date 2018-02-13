package org.sentry.nio.msg.json.encoder;

import org.apache.log4j.Logger;
import org.sentry.nio.msg.json.IJsonMsg;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * json格式编码器
 * <p>	消息长度（4字节，只包含json体的长度）	|	消息类型（4字节，对应JsonMsgTypeEnums）	|	json体 
 * 
 * @author luoyi
 *
 */
public class JsonEncoder extends MessageToByteEncoder<IJsonMsg> {

	
	static Logger log = Logger.getLogger("sentry-nio-encoder");
	
	
	//	字符编码
	protected String charactor = "UTF-8";
	
	
	public JsonEncoder() {
		super();
	}
	public JsonEncoder(String charactor) {
		super();
		this.charactor = charactor;
	}
	
	
	protected Gson gson = new Gson();
	
	
	@Override
	protected void encode(ChannelHandlerContext ctx, IJsonMsg msg, ByteBuf out) throws Exception {
		String json = gson.toJson(msg);
		byte[] bytes = json.getBytes(charactor);
		
		//	前4字节写入消息总长
		out.writeInt(bytes.length);
		//	后4字节写入消息类型
		out.writeInt(msg.type());
		//	后面追加json
		out.writeBytes(bytes);
		
		log.info("[JsonEncoder] encode msg. json:" + json);
	}

}
