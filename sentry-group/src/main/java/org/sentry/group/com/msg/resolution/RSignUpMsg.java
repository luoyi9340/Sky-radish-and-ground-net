package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 决议报名消息
 * <p>	现在要发起一项决议，询问别人要不要报名参加
 * <p>	seqId：Resolutiuon.type + '_' + Resolution.id + '_' + System.currentTimestamp
 * 
 * @author luoyi
 *
 */
public class RSignUpMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RSignUp.getVal();
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(super.toString())
				.append("[").append("]")
				.toString();
	}


}
