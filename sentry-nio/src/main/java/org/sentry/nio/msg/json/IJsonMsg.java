package org.sentry.nio.msg.json;

import java.io.Serializable;

import org.sentry.nio.msg.IMsg;


/**
 * json编解码
 * 
 * @author luoyi
 *
 */
public interface IJsonMsg extends IMsg, Serializable {

	
	/**
	 * 消息类型
	 */
	int type();
}
