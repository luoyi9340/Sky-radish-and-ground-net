package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 询问当前谁是主消息
 * 
 * @author luoyi
 *
 */
public class RAskMasterMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RAskMaster.getVal();
	}

	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(super.toString())
				.append("[]")
				.toString();
	}
}
