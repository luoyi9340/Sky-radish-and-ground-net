package org.sentry.group.com;

import org.sentry.group.com.msg.AGroupMsg;

/**
 * 等待回执消息队列
 * 
 * @author luoyi
 *
 */
public interface IWaitReceiptQueue {

	
	/**
	 * 消息扔进等待超时队列
	 */
	void append(AGroupMsg msg, long timeout, ICom.IMsgTimeoutCallback callback);
	/**
	 * 消息扔进等待超时队列，含有received回调
	 */
	void append(AGroupMsg msg, long timeout, ICom.IMsgTimeoutCallback callback, ICom.IMsgReceivedCallback receivedCallback);
	/**
	 * 删除等待超时队列
	 * <p>	删除成功则返回wait对象
	 */
	boolean remove(AGroupMsg msg);
	
	
	/**
	 * 取received回调
	 */
	ICom.IMsgReceivedCallback getReceivedCallback(AGroupMsg msg);

}
