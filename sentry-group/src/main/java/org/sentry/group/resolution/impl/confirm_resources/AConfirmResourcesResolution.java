package org.sentry.group.resolution.impl.confirm_resources;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceRespMsg;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.resolution.AComResolution;
import org.sentry.group.resolution.AComResolutionMember;
import org.sentry.group.resolution.IResolutionMember;
import org.sentry.group.resolution.ResolutionRulingTypeEnums;
import org.sentry.group.resolution.ResolutionTypeEnums;


/**
 * 确认资源是否可用决议
 * <p>	可能确认的是主节点，也可能是业务方自定义资源
 * <p>	只有超过半数的选票认为资源无效才判定为资源无效，55开的情况仍判定资源有效
 * 
 * @author luoyi
 *
 */
public abstract class AConfirmResourcesResolution extends AComResolution<ConfirmResourcesVote> {
	
	
	static Logger logConfirmResources = Logger.getLogger("sentry-resolution-confirm-resources");
	

	@Override
	public ResolutionTypeEnums type() {
		return ResolutionTypeEnums.ConfirmResource;
	}
	
	
	/**
	 * 确认资源是否有效决议，针对同一个资源的确认决议是唯一的
	 */
	@Override
	public String uniquenessId() {
		return resourceId();
	}


	/**
	 * 默认半数以上则判定为资源无效
	 */
	@Override
	public ResolutionRulingTypeEnums ruling() {
		return ResolutionRulingTypeEnums.HalfVote;
	}

	protected ConfirmResourcesVote msgConvertToVote(RConfirmResourceRespMsg msg) {
		ConfirmResourcesVote vote = new ConfirmResourcesVote();
		vote.setEffective(msg.isEffective());
		vote.setResourceId(msg.getResourceId());
		vote.setResolutionId(msg.resolutionId());
		vote.setVoteTimestamp(msg.getTimestamp());
		vote.setInfo(msg.getInfo());
		return vote;
	}
	@Override
	protected void receiveHandly(AResolutionMsg msg) {
		//	如果是投票消息
		if (msg instanceof RConfirmResourceRespMsg) {
			ConfirmResourcesVote vote = msgConvertToVote((RConfirmResourceRespMsg) msg);
			this.appendVote(members.get(vote.memberId()), vote);
		}
	}

	@Override
	protected void signUpHandler(RSignUpRespMsg msg) {
		try {
			CommunicationList.Info info = msg.getInfo();
			if (info != null) {
				ConfirmResourcesResolutuonMember member = new ConfirmResourcesResolutuonMember();
				member.setInfo(info);
				member.setResolution(this);
				member.setConnectHolder(App.ME.getCom().getConnectHolder(info));
				this.appendMember(member);
				
				logConfirmResources.info(this.toString() + " 确认资源决议 收到成员报名, 成员:" + member + " 当前成员数:" + members.size());
			}else {
				logConfirmResources.info(this.toString() + " 确认资源决议 收到成员报名, 消息中不包含对方info，本次报名无效.");
			}
		}catch (Throwable t) {
			logConfirmResources.error(this.toString() + " 处理报名消息时异常. " + t.getMessage(), t);
		}
	}

	
	/**
	 * 报名开始时候向通讯清单中每个人发送决议报名消息
	 */
	@Override
	protected void signUpStartCallback() {
		logConfirmResources.info(this.toString() + " 向通讯清单中每个人发出决议邀请. ");
		
		//	向每个人发送是否报名决议消息
		for (CommunicationList.Info info : communicationList.allInfos()) {
			RSignUpMsg msg = new RSignUpMsg();
			msg.setInfo(App.ME.self());
			msg.setResolutionId(this.id);
			msg.setResolutionType(this.type().getVal());
			msg.setSeqId(this.type().getVal() + "_" + this.id + "_" + info.toString());
			msg.setResolutionUniquenessId(uniquenessId());
			//	将发起报名时间戳发给对方，对唯一决议而言要据此判定是否融入决议
			msg.setTimestamp(this.startSignUpTimestamp());
			tempInfoMapper.put(msg.getSeqId(), info);
			
			ComConnectHolder holder = com.getConnectHolder(info);
			holder.send(msg);
			
			logConfirmResources.info(this.toString() + " 向成员:" + info.toString() + " 发出决议邀请.");
		}
		
		//	报名开始时先报自己
		ConfirmResourcesResolutuonMember me = new ConfirmResourcesResolutuonMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		appendMember(me);
		
		logConfirmResources.info(this.toString() + " 确认资源决议 报名开始时先报名自己");
	}


	@Override
	protected void voteStartCallback() {
		super.voteStartCallback();

		ConfirmResourcesResolutuonMember me = new ConfirmResourcesResolutuonMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		
		ConfirmResourcesVote vote = confirmSelf();
		vote.setInfo(App.ME.self());
		appendVote(me, vote);
		
		logConfirmResources.info(this.toString() + " 确认资源决议 投票开始时先投自己的票. vote:" + vote);
		
		logConfirmResources.info(this.toString() + " 确认资源决议 向决议其他成员广播开始投票消息.");
		//	向之前报过名的成员广播寻主消息
		if (members != null && !members.isEmpty()) {
			RConfirmResourceMsg msg = new RConfirmResourceMsg();
			msg.setInfo(App.ME.self());
			msg.setResolutionId(this.id());
			msg.setResolutionType(this.type().getVal());
			msg.setSeqId(msg.resolutionType() + "_" + msg.resolutionId() + "_" + System.currentTimeMillis());
			msg.setResolutionUniquenessId(uniquenessId());
			msg.setTimestamp(System.currentTimeMillis());
			
			msg.setResourceId(this.resourceId());
			
			for (IResolutionMember<ConfirmResourcesVote> member : members.values()) {
				//	依赖通讯完成的决议，成员一定是ACom***类型
				AComResolutionMember<ConfirmResourcesVote> comMember = (AComResolutionMember<ConfirmResourcesVote>) member;
				//	如果是自己则不予发送
				if (App.ME.isSelf(comMember.getInfo())) {continue;}
				comMember.getConnectHolder().send(msg);
				
				logConfirmResources.info(this.toString() + " 向成员:" + comMember + " 发出开始投票消息");
			}
		}
	}
	/**
	 * 确认自己的资源是否有效
	 * <p>	自己不需要再走一遍通讯流程，直接投了
	 */
	protected abstract ConfirmResourcesVote confirmSelf();
	
	
	@Override
	protected void voteEndCallback() {
		super.voteEndCallback();
		
		resourceConfirmCallback(this.result);
	}
	/**
	 * 确认资源有效与否回调
	 */
	protected abstract void resourceConfirmCallback(ConfirmResourcesVote vote);
	/**
	 * 资源id
	 * <p>	对应RConfirmResourceMsg中的resourceId
	 */
	protected abstract String resourceId();


	@Override
	protected ConfirmResourcesVote tryDecision(Collection<IResolutionMember<ConfirmResourcesVote>> members, Set<ConfirmResourcesVote> votes) {
		logConfirmResources.info(this.toString() + " 确认资源决议 尝试做出结论.");
		
		ConfirmResourcesVote res = new ConfirmResourcesVote();
		res.setEffective(true);
		res.setResourceId(this.resourceId());
		res.setResolutionId(this.id());
		
		try {
			switch (ruling()) {
				//	一票否决则只要有一个false就返回
				case OneVote: {
					for (ConfirmResourcesVote v : votes) {
						if (!v.isEffective()) {
							res.setEffective(false);
							logConfirmResources.info(this.toString() + " 确认资源决议 本决议为一票否决, 且已经收到反对票 产生最终结论. " + res);
							return res;
						}
					}
					
					//	如果所有票数与参与人数一致，说明所有人均已投过票。并且没有反对票，返回true
					if (votes.size() == members.size()) {
						logConfirmResources.info(this.toString() + " 确认资源决议 本决议为一票否决, 没有收到一张反对票. 产生最终结论. " + res);
						return res;
					}
				}
				//	半数以上则遍历所有选票，看看false票是否超过半数
				case HalfVote: {
					int unEffectiveCount = 0;
					for (ConfirmResourcesVote v : votes) {
						if (!v.isEffective()) {unEffectiveCount++;}
					}
					
					//	如果半数以上的人投反对票，则判定资源无效
					if (unEffectiveCount >= votes.size() / 2) {
						res.setEffective(false);
						logConfirmResources.info(this.toString() + " 确认资源决议 本决议为多数否决, 否决票占多数. 产生最终结论. " + res);
						return res;
					}else {
						logConfirmResources.info(this.toString() + " 确认资源决议 本决议为多数否决, 否决票占少数. 产生最终结论. " + res);
						return res;
					}
				}
				//	全票否决则遍历所有选票，只要有一个true就判定资源有效
				case AllVote: {
					for (ConfirmResourcesVote v : votes) {
						if (!v.isEffective()) {
							res.setEffective(true);
							logConfirmResources.info(this.toString() + " 确认资源决议 本决议为全票否决, 有一票为true. 产生最终结论. " + res);
							return res;
						}
					}
					
					res.setEffective(false);
					logConfirmResources.info(this.toString() + " 确认资源决议 本决议为全票否决, 全票都为false. 产生最终结论. " + res);
					return res;
				}
				//	迭代不做处理
				default: {}
			}
		}catch (Throwable t) {
			logConfirmResources.error(this.toString() + " 确认资源决议 尝试结论时出错. " + t.getMessage(), t);
		}
		
		return null;
	}

}
