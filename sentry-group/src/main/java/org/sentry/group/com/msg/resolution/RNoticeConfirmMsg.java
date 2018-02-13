package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 通知主发起关于某项资源是否可用决议
 * <p>	避针对同一资源多项决议同时发起，造成结果混乱（尤其是后续执行混乱）。
 * <p>	现在的设计中资源确认决议只能由主发起，对结果的后续执行也只能由主完成
 * 
 * @author luoyi
 *
 */
public class RNoticeConfirmMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RNoticeConfirm.getVal();
	}
	
	
}
