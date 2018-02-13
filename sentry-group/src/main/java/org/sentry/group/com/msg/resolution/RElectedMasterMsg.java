package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;
import org.sentry.group.node.CommunicationList;


/**
 * 主节点当选消息
 * <p>	改消息为广播消息，其他节点收到该消息后根据自身情况作出反馈
 * 
 * @author luoyi
 *
 */
public class RElectedMasterMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RElectedMaster.getVal();
	}
	
	//	主节点信息
	protected CommunicationList.Info master;
	
	//	当选时的决议值
	protected Long electionVal;

	public CommunicationList.Info getMaster() {
		return master;
	}
	public void setMaster(CommunicationList.Info master) {
		this.master = master;
	}
	public Long getElectionVal() {
		return electionVal;
	}
	public void setElectionVal(Long electionVal) {
		this.electionVal = electionVal;
	}
	
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(super.toString())
				.append("[")
				.append("master:").append(master)
				.append(" electionVal").append(electionVal)
				.append("]")
				.toString();
	}
}
