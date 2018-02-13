package org.sentry.group.resolution.impl.election_master;

import org.sentry.group.resolution.AComVote;


/**
 * 选主决议选票
 * 
 * @author luoyi
 *
 */
public class ElectionMasterVote extends AComVote {

	
	//	投票所在的决议id
	protected Long electionId;
	//	投票值
	protected Long electionVal;

	
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
				.append("electionId:").append(electionId).append("-")
				.append("electionVal:").append(electionVal).append("-")
				.append("]")
				.toString();
	}
}
