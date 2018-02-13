package org.sentry.nio.msg.json.decoder;

import java.util.List;

import org.apache.log4j.Logger;
import org.sentry.nio.msg.json.IJsonMsg;
import org.sentry.nio.msg.json.IMsgTypeMapper;

import com.google.gson.Gson;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;


/**
 * json消息解码
 * <p>	消息长度（4字节，只包含json体的长度）	|	消息类型（4字节，对应JsonMsgTypeEnums）	|	json体 
 * 
 * @author luoyi
 *
 */
public class JsonDecoder extends ByteToMessageDecoder {
	
	
	static Logger log = Logger.getLogger("sentry-nio-decoder");
	
	
	//	字符编码
	protected String charactor = "UTF-8";
	//	mapper
	protected IMsgTypeMapper mapper;
	
	protected Gson gson = new Gson();
	
	
	public JsonDecoder() {
		super();
	}
	public JsonDecoder(String charactor, IMsgTypeMapper mapper) {
		super();
		this.charactor = charactor;
		this.mapper = mapper;
	}
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int canReadlength = in.readableBytes();
		//	如果接收到的消息总长小于8字节则不予读取（消息总长 + 消息类型）
		if (canReadlength < 8) {return ;}
		
		//	标记读开始位置，有可能接收的二进制不足导致回滚到这里
		in.markReaderIndex();
		
		in.markWriterIndex();
		//	读取消息总长
		int length = in.readInt();
		//	如果剩下的消息长度不足，则不予读取，并且回滚（余下的二进制位数应包括消息类型4字节）
		if (canReadlength < length + 4) {
			in.resetReaderIndex();
			return;
		}
		//	读取消息类型
		int type = in.readInt();
		Class<?> clazz = mapper.mapper(type);
		//	拿不到消息类型则直接丢弃掉本消息
		if (clazz == null) {
			//	读掉生效的二进制
			in.readBytes(length);
			ctx.fireExceptionCaught(new DecoderException("unknow msg type. type:" + type));
			
			log.warn("[JsonDecoder] decode unknow msg type. type:" + type);
			return;
		}
		
		//	读取json传内容
		byte[] bytes = new byte[length];
		in.readBytes(bytes);
		String json = new String(bytes, charactor);
		IJsonMsg body = (IJsonMsg) gson.fromJson(json, clazz);
		out.add(body);
		
		log.info("[JsonDecoder] decode msg. json:" + json);
		
		//	如果余下的二进制位仍然够一条，则继续解析
		if (in.readableBytes() > 8) {
			this.decode(ctx, in, out);
		}
	}


}
