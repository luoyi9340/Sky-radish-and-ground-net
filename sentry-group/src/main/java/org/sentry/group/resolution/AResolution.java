package org.sentry.group.resolution;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

/**
 * 决议第一层封装
 * <p>	每轮决议流程：
 * <p>	有发起决议方向其他人广播决议开始消息，接收人收到消息后决定是否参加决议，并回复
 * <p>	若发起人收到的是参加决议，则接收人加入决议。
 * <p>	若发起人直接收到决议选票，则也判定接收人加入决议（考虑网络延迟的情况）
 * 
 * <p>	每轮决议分为两步：
 * <p>	step1:	报名阶段，等待其他人报名参加决议。过期不候
 * <p>	step2:	投票阶段，等待其他人投票结果。并根据决议类型产生最终决定。过期不候
 * 
 * @author luoyi
 *
 */
public abstract class AResolution<T extends IVote> implements IResolution<T> {

	
	static Logger log = Logger.getLogger("sentry-resolution");
	

	//	默认的决议id是时间戳
	protected long id = System.currentTimeMillis();
	
	//	报名是否截止
	protected volatile boolean signUpEnd;
	//	报名线程
	protected Thread signUpThread;
	//	投票是否截止
	protected volatile boolean voteEnd = true;
	//	投票线程
	protected Thread voteThread;
	
	//	决议是否被终止
	protected boolean undo = false;
	
	//	发报名时间戳
	protected long startSignUpTimestamp;
	//	报名到期时间间隔（从startResolutionTimestamp开始算），到了到期时间即便收到参加决议信息也忽略（单位：毫秒，-1表示永远等下去）
	protected long singUpEndTimeInterval;
	//	开启头投票时间戳
	protected long startVoteTimestamp;
	//	决议到期间隔（从startVoteTimestamp开始算），到了到期时间则必须产生决定，无决定也是一种结果（单位：毫秒，-1表示永远等下去）
	protected long voteEndTimeInterval;
	//	决议结束时间戳
	protected long endTimestamp;
	//	最终结果
	protected T result;
	
	
	//	参与决议的成员
	protected Map<String, IResolutionMember<T>> members = new HashMap<String, IResolutionMember<T>>();
	//	收到的选票（按投票时间升序）
	protected Set<T> votes = new TreeSet<T>(new Comparator<T>() {
		public int compare(T o1, T o2) {
			if (o1.memberId().equals(o2.memberId())
					&& o1.resolutionId() == o2.resolutionId()) {return 0;}
			return o1.voteTimestamp() < o2.voteTimestamp() ? -1 : 1;
		}
	});
	
	//	产生决定回调
	protected IDecisionListener<T> decisionListener;
	@Override
	public void addDecisionListener(IDecisionListener<T> listener) {
		this.decisionListener = listener;
	}
	
	
	/**
	 * 默认的唯一标识是type + id
	 * <p>	这样的标识不具备唯一性。id是自增的
	 */
	@Override
	public String uniquenessId() {
		return type().getCode() + "-" + id();
	}


	/**
	 * 投票监听动作
	 */
	public void appendVote(IResolutionMember<T> member, T vote) {
		//	如果选票不是本轮决议，则忽略
		if (vote.resolutionId() != id()) {
			log.info(this.toString() + " 收到选票，但不是本轮决议的选票，不予处理.");
			return;
		}
		
		//	如果投票已截止，则不再计算决定
		if (isVoteEnd()) {
			log.info(this.toString() + " 收到选票，但投票已终止，不予处理.");
			return;
		}
		//	重置投票时间为发起人接收时间（可能某些业务要重写投票时间）
		if (vote instanceof AVote) {
			((AVote)vote).setVoteTimestamp(System.currentTimeMillis());
		}
		
		//	如果members中不包含投票人信息，则先追加投票人
		if (!members.containsKey(member.id())) {members.put(member.id(), member);}
		
		//	追加收到的决议集合
		votes.add(vote);
		
		log.info(this.toString() + " 收到选票，记录之. vote:" + vote);
		
		//	如果是一票通过，则收到消息，立即终止投票发起裁决
		if (ruling().equals(ResolutionRulingTypeEnums.OneVote)) {
			//	尝试产生结果
			T v = tryDecision(members.values(), votes);
			if (v != null) {
				this.result = v;
				terminationOfVote();
			}
		}
		//	如果是全票通过或者半数通过，则收到的选票数 == 参与决议数，立即终止投票发起裁决
		else if ((ruling().equals(ResolutionRulingTypeEnums.AllVote) || ruling().equals(ResolutionRulingTypeEnums.HalfVote))
				&&
				(votes.size() == members.size())) {
			terminationOfVote();
		}
		//	如果依赖每次的投票产生
		else if (ruling().equals(ResolutionRulingTypeEnums.Every)) {
			//	尝试产生结果
			T v = tryDecision(members.values(), votes);
			if (v != null) {
				this.result = v;
				terminationOfVote();
			}
		}
	}
	
	
	/**
	 * 追加成员
	 */
	@Override
	public void appendMember(IResolutionMember<T> member) {
		if (isSignUpEnd()) {
			log.info(this.toString() + " 收到报名，但报名还未开始或已截止，不予处理. member:" + member.toString());
			return;
		}
		
		member.setResolution(this);
		
		members.put(member.id(), member);
		
		log.info(this.toString() + " 收到报名，记录之. member:" + member.toString());
	}
	
	
	/**
	 * 产生决定
	 * <p>	超过投票时间，或所有人均投票完成时调用
	 * 
	 * @return	是否产生结果
	 */
	protected void decision() {
		if (decisionListener != null) {decisionListener.decisionFinished();}
		
		//	如果投票还未终止，则不产生决定
		if (!isVoteEnd()) {
			log.info(this.toString() + " 投票还未截止，不产生结论. ");
			return ;
		}
		
		//	如果已经产生决定，则直接结束
		if (result != null) {
			log.info(this.toString() + " 已产生结论，不予重复产生.");
			
			voteEnd = true;
			endTimestamp = System.currentTimeMillis();
			return;
		}
		//	尝试产生决定
		T vote = tryDecision(members.values(), votes);
		
		if (vote != null) {
			log.info(this.toString() + " 产生结论:" + vote.toString());
			
			result = vote;
			voteEnd = true;
			endTimestamp = System.currentTimeMillis();
			return ;
		}
		log.info(this.toString() + " 未产生结论");
		
		return ;
	}


	/**
	 * 汇总选票是否产生结果
	 */
	protected abstract T tryDecision(Collection<IResolutionMember<T>> members, Set<T> votes);
	
	
	
	/**
	 * 发起决议报名
	 * <p>	过期不候
	 */
	@Override
	public void startSignUp() {
		if (signUpThread == null) {
			signUpThread = new Thread() {
				public void run() {
					//	不管是睡醒还是唤醒，军判断决议是否终止
					if (undo) {
						log.info(this.toString() + " 被终止，报名取消.");
						return;
					}
					
					log.info(this.toString() + " 开始报名. ");
					//	记录报名开始时间戳
					AResolution.this.startSignUpTimestamp = System.currentTimeMillis();
					
					AResolution.this.signUpEnd = false;
					//	执行报名开始回调
					AResolution.this.signUpStartCallback();
					
					//	等待报名
					synchronized (this) {
						try {
							this.wait(AResolution.this.singUpEndTimeInterval);
							
							AResolution.this.signUpEnd = true;
						} catch (InterruptedException e) {
							log.error(this.toString() + " signUpThread error. " + e.getMessage(), e);
							return;
						}
					}
					//	不管是睡醒还是唤醒，军判断决议是否终止
					if (undo) {
						log.info(this.toString() + " 决议终止，报名取消. ");
						return;
					}
					
					//	执行报名截止回到
					AResolution.this.signUpEndCallback();
					//	发起投票
					AResolution.this.startVote();
					
					log.info(this.toString() + " 报名完成. 成员数:" + members.size());
				}
			};
			signUpThread.start();
		}
	}
	/**
	 * 终止决议
	 */
	@Override
	public void undo() {
		this.undo = true;
		
		//	如果已经开始报名则终止报名
		if (!this.signUpEnd) {
			terminationOfSignUp();
		}
		
		//	如果已经开始投票则终止投票
		if (!this.voteEnd) {
			terminationOfVote();
		}
	}
	/**
	 * 报名开始回调
	 */
	protected abstract void signUpStartCallback();
	/**
	 * 报名截止回调
	 */
	protected abstract void signUpEndCallback();
	/**
	 * 报名截止，开始投票
	 */
	protected void terminationOfSignUp() {
		if (signUpThread != null
				&& !isSignUpEnd()) {
			synchronized (signUpThread) {
				try {
					signUpThread.notify();
				}catch (Exception e) {
					log.error("[AResolution] terminationOfSignUp error. " + e.getMessage(), e);
				}
			}
		}
	}
	/**
	 * 发起决议投票
	 * <p>	过期不候
	 */
	protected void startVote() {
		if (voteThread == null) {
			voteThread = new Thread() {
				public void run() {
					//	不管是睡醒还是唤醒，军判断决议是否终止
					if (undo) {
						log.info(this.toString() + " 决议终止，投票取消. ");
						return;
					}
					
					log.info(this.toString() + " 开始投票.");
					//	记录开始投票时间戳
					AResolution.this.startVoteTimestamp = System.currentTimeMillis();
					
					AResolution.this.voteEnd = false;
					//	执行投票开始回调
					AResolution.this.voteStartCallback();
					
					//	等待投票截止
					synchronized (this) {
						try {
							this.wait(AResolution.this.voteEndTimeInterval);
							
							AResolution.this.voteEnd = true;
						} catch (InterruptedException e) {
							log.error("[AResolution] signUpThread error. " + e.getMessage(), e);
							return;
						}
					}
					//	不管是睡醒还是唤醒，军判断决议是否终止
					if (undo) {
						log.info(this.toString() + " 决议终止，投票取消. ");
						return;
					}
					
					//	产生投票结果
					AResolution.this.decision();
					//	执行投票截止回调
					AResolution.this.voteEndCallback();
					
					log.info(this.toString() + " 投票截止. ");
				}
			};
			voteThread.start();
		}
	}
	/**
	 * 投票开始回调
	 */
	protected abstract void voteStartCallback();
	/**
	 * 投票截止回调
	 */
	protected abstract void voteEndCallback();
	/**
	 * 投票截止，开始产生决议
	 */
	protected void terminationOfVote() {
		if (voteThread != null
				&& !isVoteEnd()) {
			synchronized (voteThread) {
				try {
					voteThread.notify();
				}catch (Exception e) {
					log.error("[AResolution] terminationOfVote error. " + e.getMessage(), e);
				}
			}
		}
	}


	/**
	 * 取最终决定
	 */
	@Override
	public T result() {
		return result;
	}
	
	
	/**
	 * 默认的决议id是时间戳
	 */
	@Override
	public long id() {
		return id;
	}
	@Override
	public boolean isVoteEnd() {
		return voteEnd;
	}
	public boolean isSignUpEnd() {
		return signUpEnd;
	}
	@Override
	public long startSignUpTimestamp() {
		return startSignUpTimestamp;
	}
	@Override
	public long startVoteTimestamp() {
		return startVoteTimestamp;
	}
	@Override
	public long endTimestamp() {
		return endTimestamp;
	}
	@Override
	public void setSignUpInterval(long interval) {
		this.singUpEndTimeInterval = interval;
	}
	@Override
	public void setVoteInterval(long interval) {
		this.voteEndTimeInterval = interval;
	}


	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("决议:").append(this.type()).append("-")
				.append("id:").append(this.id()).append("-")
				.append("ruling:").append(this.ruling())
				.append("]").toString();
	}
	
}
