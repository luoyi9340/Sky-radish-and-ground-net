package org.sentry.group.com.wait_receipt_queue;

import org.sentry.group.com.ICom;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.node.CommunicationList;

/**
 * 等待回执队列中的等待对象
 * 
 * @author luoyi
 *
 */
public class WaitVo {

	
	//	消息
	protected AGroupMsg msg;
	protected CommunicationList.Info info;
	
	//	超时时间戳
	protected long timeout;
	
	//	超时执行回调
	protected ICom.IMsgTimeoutCallback timeoutCallback;
	//	正常收到消息回调

	public AGroupMsg getMsg() {
		return msg;
	}

	public void setMsg(AGroupMsg msg) {
		this.msg = msg;
	}

	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public ICom.IMsgTimeoutCallback getTimeoutCallback() {
		return timeoutCallback;
	}

	public void setTimeoutCallback(ICom.IMsgTimeoutCallback timeoutCallback) {
		this.timeoutCallback = timeoutCallback;
	}

	@Override
	public int hashCode() {
		return msg.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WaitVo)) {return false;}
		
		WaitVo vo = (WaitVo) obj;
		if (vo.getMsg() == null) {return false;}
		return this.getMsg().equals(vo.getMsg());
	}
	
	
	
}
