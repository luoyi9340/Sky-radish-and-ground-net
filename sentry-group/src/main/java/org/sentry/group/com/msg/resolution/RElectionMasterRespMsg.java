package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 选主决议消息
 * 
 * @author luoyi
 *
 */
public class RElectionMasterRespMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RElectionMasterResp.getVal();
	}
	
	
	//	自己的决议值（摇色子的点数）
	protected Long electionVal;
	

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
				.append("electionVal:").append(electionVal)
				.append("]")
				.toString();
	}

}
