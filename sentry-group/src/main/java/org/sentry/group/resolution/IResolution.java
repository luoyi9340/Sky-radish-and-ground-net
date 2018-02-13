package org.sentry.group.resolution;


/**
 * 决议
 * 
 * @author luoyi
 *
 */
public interface IResolution<T extends IVote> {

	
	/**
	 * 	决议id
	 */
	long id();
	/**
	 * 决议唯一性标识
	 * <p>	同一时刻只允许一个uniquenessId的决议在集群中进行
	 * <p>	如：寻主/选主/针对同一资源确认是否有效
	 */
	String uniquenessId();
	/**
	 * 	决议类型
	 */
	ResolutionTypeEnums type();
	/**
	 * 裁决类型
	 */
	ResolutionRulingTypeEnums ruling();
	/**
	 * 报名是否截止
	 */
	boolean isSignUpEnd();
	/**
	 * 投票是否截止
	 */
	boolean isVoteEnd();
	/**
	 * 最终决定
	 */
	T result();
	
	
	/**
	 * 追加决议成员
	 */
	void appendMember(IResolutionMember<T> member);
	/**
	 * 追加投票
	 */
	void appendVote(IResolutionMember<T> member, T vote);
	/**
	 * 追加决定监听
	 */
	void addDecisionListener(IDecisionListener<T> listener);
	
	
	/**
	 * 开启决议
	 * <p>	从报名开始
	 */
	void startSignUp();
	/**
	 * 终止决议
	 */
	void undo();
	
	
	/**
	 * 	开启决议时间戳（开始向外发起决议的时间戳）
	 */
	long startSignUpTimestamp();
	/**
	 * 设置报名截止时间（单位：毫秒）
	 */
	void setSignUpInterval(long interval);
	/**
	 * 开启投票时间戳
	 */
	long startVoteTimestamp();
	/**
	 * 设置投票截止时间（单位：毫秒）
	 */
	void setVoteInterval(long interval);
	/**
	 * 	结束时间戳
	 */
	long endTimestamp();
	
	
	/**
	 * 产生决定回调
	 */
	public static interface IDecisionListener<T extends IVote> {
		/**
		 * 产生决定
		 */
		void decisionFinished();
	}
}
