package org.sentry.nio.handly.callback;

import org.sentry.nio.msg.json.IJsonMsg;


/**
 * 写入消息回调
 * 
 * @author luoyi
 *
 * @param <T>
 */
public interface IMsgWriteCallback<T extends IJsonMsg> extends IMsgCallback<T>{

}
