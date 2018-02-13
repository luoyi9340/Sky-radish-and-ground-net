package org.sentry.group.resolution.impl.ask_master;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.resolution.RAskMasterMsg;
import org.sentry.group.com.msg.resolution.RAskMasterRespMsg;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.node.MasterInfo;
import org.sentry.group.resolution.AComResolution;
import org.sentry.group.resolution.AComResolutionMember;
import org.sentry.group.resolution.IResolutionMember;
import org.sentry.group.resolution.ResolutionRulingTypeEnums;
import org.sentry.group.resolution.ResolutionTypeEnums;


/**
 * 询问谁是当前主决议
 * <p>	半数以上通过，若出现多个票数相同。则以先拿到多数票的为主
 * 
 * @author luoyi
 *
 */
public class AskMasterResolution extends AComResolution<AskMasterVote> {

	
	static Logger logAskMaster = Logger.getLogger("sentry-resolution-ask-master");
	
	
	public static final String uniquenessId = "ask-master";
	
	
	/**
	 * 寻主决议是唯一的，不然寻出来的主太多会乱掉
	 * @return
	 */
	@Override
	public String uniquenessId() {
		return uniquenessId;
	}


	/**
	 * 尝试得出决定
	 */
	@Override
	protected AskMasterVote tryDecision(Collection<IResolutionMember<AskMasterVote>> members, Set<AskMasterVote> votes) {
		//	从收到的选票中找出最新的且决议值最大的选票（决议id最大的，且决议值最大）
		logAskMaster.info(this.toString() + " 尝试产生结论. 当前收到的票数:" + votes.size());
		AskMasterVote vote = null;
		for (AskMasterVote v : votes) {
			if (vote == null) {vote = v;}
			else {
				if (v.getElectionId().compareTo(vote.getElectionId()) >= 0
						&& (v.getElectionVal().compareTo(vote.getElectionVal()) > 0)) {
					vote = v;
				}
			}
		}
		logAskMaster.info(this.toString() + " 本轮尝试产生的结论:" + vote);
		return vote;
	}
	
	
	@Override
	public ResolutionTypeEnums type() {
		return ResolutionTypeEnums.AskMaster;
	}

	@Override
	public ResolutionRulingTypeEnums ruling() {
		return ResolutionRulingTypeEnums.HalfVote;
	}

	
	protected AskMasterVote msgConvertToVote(RAskMasterRespMsg msg) {
		//	根据应答消息生成选票
		AskMasterVote vote = new AskMasterVote();
		vote.setElectionId(msg.getElectionId());
		vote.setElectionVal(msg.getElectionVal());
		vote.setInfo(msg.getInfo());
		vote.setResolutionId(msg.resolutionId());
		vote.setVoteTimestamp(System.currentTimeMillis());
		vote.setMaster(msg.getMaster());
		return vote;
	}
	@Override
	protected void receiveHandly(AResolutionMsg msg) {
		//	如果收到的是投票消息
		if (msg instanceof RAskMasterRespMsg) {
			AskMasterVote vote = msgConvertToVote((RAskMasterRespMsg) msg);
			this.appendVote(members.get(vote.memberId()), vote);
		}
	}
	@Override
	protected void signUpHandler(RSignUpRespMsg msg) {
		CommunicationList.Info info = msg.getInfo();
		if (info != null) {
			AskMasterResolutionMember member = new AskMasterResolutionMember();
			member.setInfo(info);
			member.setResolution(this);
			member.setConnectHolder(App.ME.getCom().getConnectHolder(info));
			this.appendMember(member);
			
			logAskMaster.info(this.toString() + " 收到成员报名, member:" + member + " 当前成员数:" + members.size());
		}
	}

	
	/**
	 * 报名开始时候向通讯清单中每个人发送决议报名消息
	 */
	@Override
	protected void signUpStartCallback() {
		logAskMaster.info(this.toString() + " 向通讯清单中每个人发出决议邀请. ");
		
		RSignUpMsg msg = new RSignUpMsg();
		msg.setInfo(App.ME.self());
		msg.setResolutionId(this.id);
		msg.setResolutionType(this.type().getVal());
		msg.setSeqId(this.type().getVal() + "_" + this.id);
		msg.setResolutionUniquenessId(uniquenessId());
		msg.setInfo(App.ME.self());
		//	将发起报名时间戳发给对方，对唯一决议而言要据此判定是否融入决议
		msg.setTimestamp(this.startSignUpTimestamp());
		//	向每个人发送是否报名决议消息
		for (CommunicationList.Info info : communicationList.allInfos()) {
			tempInfoMapper.put(msg.getSeqId(), info);
			
			ComConnectHolder holder = com.getConnectHolder(info);
			holder.send(msg);
			
			logAskMaster.info(this.toString() + " 向成员:" + info.toString() + " 发出决议邀请.");
		}
		
		//	报名开始后先报名自己
		AskMasterResolutionMember me = new AskMasterResolutionMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		appendMember(me);
		
		logAskMaster.info(this.toString() + " 寻主决议 报名开始时先报名自己.");
	}

	
	@Override
	protected void voteStartCallback() {
		super.voteStartCallback();
		
		//	投票开始后先头自己一票
		AskMasterResolutionMember me = new AskMasterResolutionMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		
		AskMasterVote vote = new AskMasterVote();
		vote.setResolutionId(this.id);
		vote.setInfo(App.ME.self());
		vote.setMaster(App.ME.self());
		vote.setElectionId(-1l);
		vote.setElectionVal(RandomUtils.randomElection());
		
		appendVote(me, vote);
		logAskMaster.info(this.toString() + " 投票开始时先投自己的票. vote:" + vote);
		
		logAskMaster.info(this.toString() + " 向决议其他成员广播开始投票消息.");
		//	向之前报过名的成员广播寻主消息
		if (members != null && !members.isEmpty()) {
			RAskMasterMsg msg = new RAskMasterMsg();
			msg.setInfo(App.ME.self());
			msg.setResolutionId(this.id());
			msg.setResolutionType(this.type().getVal());
			msg.setSeqId(msg.resolutionType() + "_" + msg.resolutionId() + "_" + System.currentTimeMillis());
			msg.setResolutionUniquenessId(uniquenessId());
			msg.setTimestamp(System.currentTimeMillis());
			
			for (IResolutionMember<AskMasterVote> member : members.values()) {
				//	依赖通讯完成的决议，成员一定是ACom***类型
				AComResolutionMember<AskMasterVote> comMember = (AComResolutionMember<AskMasterVote>) member;
				//	如果是自己则不予发送
				if (App.ME.isSelf(comMember.getInfo())) {continue;}
				comMember.getConnectHolder().send(msg);
				
				logAskMaster.info(this.toString() + " 向成员:" + comMember + " 发出开始投票消息.");
			}
		}
	}


	@Override
	protected void voteEndCallback() {
		super.voteEndCallback();
		
		//	根据决议的最终结果生成主
		AskMasterVote vote = result();
		
		//	如果产生了主，则判断主是否是自己。从而决定是跟随还是广播自己是主
		if (vote != null) {
			MasterInfo master = new MasterInfo();
			master.setElectionId(vote.getElectionId());
			master.setElectionVal(vote.getElectionVal());
			master.setInfo(vote.getMaster());
			master.setTimestamp(System.currentTimeMillis());
			App.ME.resetMaster(master);
			
			logAskMaster.info(this.toString() + " 寻主决议产生结论, 当前主:" + master.getInfo() + " ，公开之前的所有投票.");
			//	打印所有选票
			for (AskMasterVote v : votes) {
				logAskMaster.info(this.toString() + v);
			}
			
			//	广播主当选消息（向通讯清单中所有人广播）
			RElectedMasterMsg elected = new RElectedMasterMsg();
			elected.setMaster(master.getInfo());
			elected.setResolutionId(this.id());
			elected.setSeqId("Elected-" + System.currentTimeMillis());
			elected.setInfo(App.ME.self());
			elected.setElectionVal(master.getElectionVal());
			for (CommunicationList.Info info : App.ME.getCommunicationList().allInfos()) {
				ComConnectHolder comHolder = App.ME.getCom().getConnectHolder(info);
				comHolder.send(elected);
			}
		}else {
			logAskMaster.info(this.toString() + " 寻主决议未产生结论，公开之前的所有投票.");
			for (AskMasterVote v : votes) {
				logAskMaster.info(this.toString() + v);
			}
		}
	}
	
}
