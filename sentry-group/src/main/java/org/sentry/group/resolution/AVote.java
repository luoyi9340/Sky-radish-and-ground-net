package org.sentry.group.resolution;


/**
 * 默认的选票
 * 
 * @author luoyi
 *
 */
public abstract class AVote implements IVote {

	
	//	本次决议id
	protected long resolutionId;
	
	//	投票时间
	protected long voteTimestamp;

	
	
	@Override
	public long resolutionId() {
		return resolutionId;
	}
	public void setResolutionId(long resolutionId) {
		this.resolutionId = resolutionId;
	}

	public void setVoteTimestamp(long timesstamp) {
		this.voteTimestamp = timesstamp;
	}
	public long voteTimestamp() {
		return voteTimestamp;
	}
	
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("决议id:").append(this.resolutionId()).append("-")
				.append("成员id:").append(this.memberId()).append("-")
				.append("]")
				.toString();
	}

}
