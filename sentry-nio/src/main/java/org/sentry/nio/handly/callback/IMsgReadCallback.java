package org.sentry.nio.handly.callback;

import org.sentry.nio.msg.json.IJsonMsg;


/**
 * 收到消息回调
 * 
 * @author luoyi
 *
 * @param <T>
 */
public interface IMsgReadCallback<T extends IJsonMsg> extends IMsgCallback<T>{

	

}
