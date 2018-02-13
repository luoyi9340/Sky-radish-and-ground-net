package org.sentry.group.com.msg;

import org.sentry.nio.msg.json.IJsonMsg;
import org.sentry.nio.msg.json.IMsgTypeMapper;


/**
 * 消息类型mapper
 * 
 * @author luoyi
 *
 */
public class MsgTypeMapperImpl implements IMsgTypeMapper {

	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IJsonMsg> Class<T> mapper(int type) {
		MsgTypeEnums mt = MsgTypeEnums.getInstance(type);
		return (Class<T>) (mt != null ? mt.getClazz() : null);
	}

}
