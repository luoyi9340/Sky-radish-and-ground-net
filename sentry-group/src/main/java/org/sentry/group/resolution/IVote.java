package org.sentry.group.resolution;


/**
 * 选票
 * 
 * @author luoyi
 *
 */
public interface IVote {
	
	
	/**
	 * 决议id
	 */
	long resolutionId();
	
	
	/**
	 * 投票成员id
	 */
	String memberId();
	
	
	/**
	 * 投票时间戳
	 * <p>	以服务端收到投票的时间戳为准
	 */
	long voteTimestamp();
}
