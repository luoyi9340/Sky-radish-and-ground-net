package org.sentry.group.resolution.impl.ask_master;

import org.sentry.group.node.CommunicationList;
import org.sentry.group.resolution.AComVote;


/**
 * 询问当前主决议选票
 * 
 * @author luoyi
 *
 */
public class AskMasterVote extends AComVote {
	
	
	//	当前主消息
	protected CommunicationList.Info master;
	//	当前主当选时的决议id
	protected Long electionId;
	//	当前主当选时的权值
	protected Long electionVal;
	
	
	@Override
	public long voteTimestamp() {
		return voteTimestamp;
	}

	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}

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
				.append("[")
				.append("决议id:").append(this.resolutionId()).append("-")
				.append("成员id:").append(this.memberId()).append("-")
				.append("electionId:").append(this.electionId).append("-")
				.append("electionVal:").append(this.electionVal).append("-")
				.append("]")
				.toString();
	}

}
