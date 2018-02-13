package org.sentry.nio.handly.callback;

import org.sentry.nio.msg.json.IJsonMsg;


/**
 * 回调IJsonMsg与ICallback mapper
 * 
 * @author luoyi
 *
 */
public interface ICallbackMapper {

	
	/**
	 * 读到消息类型与各种callback mapper
	 */
	<T extends IJsonMsg> IMsgReadCallback<T> mapperRead(IJsonMsg msg);
	
	
	/**
	 * 写入消息类型与各种callback mapper
	 */
	<T extends IJsonMsg> IMsgWriteCallback<T> mapperWrite(IJsonMsg msg);
}
