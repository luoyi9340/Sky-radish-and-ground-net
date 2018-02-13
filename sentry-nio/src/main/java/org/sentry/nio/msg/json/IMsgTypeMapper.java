package org.sentry.nio.msg.json;


/**
 * type与msg mapper
 * 
 * @author luoyi
 *
 */
public interface IMsgTypeMapper {

	
	<T extends IJsonMsg> Class<T> mapper(int type);
}
