package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;
import org.sentry.group.node.CommunicationList;


/**
 * 询问当前谁是主消息应答
 * 
 * @author luoyi
 *
 */
public class RAskMasterRespMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RAskMasterResp.getVal();
	}
	
	//	当前主信息
	protected CommunicationList.Info master;
	//	当前主当选时的决议id
	protected Long electionId;
	//	当前主当选时的权值
	protected Long electionVal;

	public CommunicationList.Info getMaster() {
		return master;
	}
	public void setMaster(CommunicationList.Info master) {
		this.master = master;
	}
	public Long getElectionId() {
		return electionId;
	}
	public void setElectionId(Long electionId) {
		this.electionId = electionId;
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
				.append(" electionId:").append(electionId)
				.append(" electionVal:").append(electionVal)
				.append("]")
				.toString();
	}
}
