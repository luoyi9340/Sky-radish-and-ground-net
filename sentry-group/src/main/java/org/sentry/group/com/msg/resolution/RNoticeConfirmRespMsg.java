package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 通知主发起关于某项资源是否可用决议的应答
 * 
 * @author luoyi
 *
 */
public class RNoticeConfirmRespMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RNoticeConfirm.getVal();
	}
	
	
	//	资源唯一标识
	protected String uniquenessId;
	
	//	主是否受理
	protected boolean accept;

	//	当前被询问的节点不是主（客户端收到此消息时退回Looking状态，并发起寻主决议）
	protected boolean noMaster;
	
	//	未受理的原因
	protected String remark;
	
	public String getUniquenessId() {
		return uniquenessId;
	}

	public void setUniquenessId(String uniquenessId) {
		this.uniquenessId = uniquenessId;
	}

	public boolean isAccept() {
		return accept;
	}

	public void setAccept(boolean accept) {
		this.accept = accept;
	}

	public boolean isNoMaster() {
		return noMaster;
	}

	public void setNoMaster(boolean noMaster) {
		this.noMaster = noMaster;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
