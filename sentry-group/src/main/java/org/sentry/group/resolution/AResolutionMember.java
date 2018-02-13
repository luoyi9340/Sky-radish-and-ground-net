package org.sentry.group.resolution;

import org.apache.log4j.Logger;

/**
 * 参与人第一层封装
 * <p>	这里的参与人只是实际参与人在决议发起人那里的一个代理，实际参与人可能在网络另一端
 * 
 * @author luoyi
 *
 * @param <T>
 */
public abstract class AResolutionMember<T extends IVote> implements IResolutionMember<T> {

	
	static Logger log = Logger.getLogger("sentry-resolution-member");
	
	
	//	决议
	protected IResolution<T> resolution;
	
	
	//	最终选票
	protected T finalVote;
	
	
	@Override
	public T voteFinial() {
		return finalVote;
	}
	
	
	@Override
	public IResolution<T> resolution() {
		return resolution;
	}
	public void setResolution(IResolution<T> resolution) {
		this.resolution = resolution;
	}

}
