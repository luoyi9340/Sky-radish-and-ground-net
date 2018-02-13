package org.sentry.group.resolution;

/**
 * 决议成员
 * 
 * @author luoyi
 *
 */
public interface IResolutionMember<T extends IVote> {

	
	/**
	 * 身份标识
	 */
	String id();
	/**
	 * 所属决议
	 */
	IResolution<T> resolution();
	void setResolution(IResolution<T> resolution);
	
	
	/**
	 * 最终投票
	 */
	T voteFinial();
	
}
