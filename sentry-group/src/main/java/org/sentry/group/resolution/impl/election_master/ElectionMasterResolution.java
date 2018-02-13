package org.sentry.group.resolution.impl.election_master;

import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterRespMsg;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.node.MasterInfo;
import org.sentry.group.resolution.AComResolution;
import org.sentry.group.resolution.AComResolutionMember;
import org.sentry.group.resolution.IResolutionMember;
import org.sentry.group.resolution.ResolutionRulingTypeEnums;
import org.sentry.group.resolution.ResolutionTypeEnums;


/**
 * 选主决议
 * 
 * @author luoyi
 *
 */
public class ElectionMasterResolution extends AComResolution<ElectionMasterVote> {

	
	static Logger log = Logger.getLogger("sentry-resolution");
	static Logger logGroup = Logger.getLogger("sentry-group");
	static Logger logElectionMaster = Logger.getLogger("sentry-resolution-election-master");
	
	
	@Override
	public ResolutionTypeEnums type() {
		return ResolutionTypeEnums.ElectionMaster;
	}
	

	public static final String uniquenessId = "election-master";
	
	
	/**
	 * 选主决议同一时刻也是唯一的，不然会选出多个主
	 */
	@Override
	public String uniquenessId() {
		return uniquenessId;
	}


	@Override
	public ResolutionRulingTypeEnums ruling() {
		return ResolutionRulingTypeEnums.HalfVote;
	}

	protected ElectionMasterVote msgConvertToVote(RElectionMasterRespMsg msg) {
		ElectionMasterVote vote = new ElectionMasterVote();
		vote.setResolutionId(msg.resolutionId());
		vote.setInfo(msg.getInfo());
		vote.setVoteTimestamp(System.currentTimeMillis());
		vote.setElectionVal(msg.getElectionVal());
		return vote;
	}

	@Override
	protected void receiveHandly(AResolutionMsg msg) {
		//	如果收到的是投票消息
		if (msg instanceof RElectionMasterRespMsg) {
			ElectionMasterVote vote = msgConvertToVote((RElectionMasterRespMsg) msg);
			this.appendVote(members.get(vote.memberId()), vote);
		}
	}

	
	/**
	 * 报名开始时候向通讯清单中每个人发送决议报名消息
	 */
	@Override
	protected void signUpStartCallback() {
		logElectionMaster.info(this.toString() + " 向通讯清单中每个人发出决议邀请. ");
		
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
			
			logElectionMaster.info(this.toString() + " 向成员:" + info.toString() + " 发出决议邀请.");
		}
		
		//	报名开始后先报名自己
		ElectionMasterResolutionMember me = new ElectionMasterResolutionMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		appendMember(me);
		
		logElectionMaster.info(this.toString() + " 选主决议 报名开始时先报名自己");
	}

	
	@Override
	protected void voteStartCallback() {
		super.voteStartCallback();
		
		//	投票开始后先头自己一票
		ElectionMasterResolutionMember me = new ElectionMasterResolutionMember();
		me.setResolution(this);
		me.setConnectHolder(null);
		me.setInfo(App.ME.self());
		
		ElectionMasterVote vote = new ElectionMasterVote();
		vote.setResolutionId(this.id);
		vote.setInfo(App.ME.self());
		vote.setElectionId(this.id());
		vote.setElectionVal(RandomUtils.randomElection());
		
		appendVote(me, vote);
		
		logElectionMaster.info(this.toString() + " 选主决议 向决议其他成员广播开始投票消息.");
		//	向之前报过名的成员广播寻主消息
		if (members != null && !members.isEmpty()) {
			RElectionMasterMsg msg = new RElectionMasterMsg();
			msg.setInfo(App.ME.self());
			msg.setResolutionId(this.id());
			msg.setResolutionType(this.type().getVal());
			msg.setSeqId(msg.resolutionType() + "_" + msg.resolutionId() + "_" + System.currentTimeMillis());
			msg.setResolutionUniquenessId(uniquenessId());
			msg.setTimestamp(System.currentTimeMillis());
			
			for (IResolutionMember<ElectionMasterVote> member : members.values()) {
				//	依赖通讯完成的决议，成员一定是ACom***类型
				AComResolutionMember<ElectionMasterVote> comMember = (AComResolutionMember<ElectionMasterVote>) member;
				//	如果是自己则不予发送
				if (App.ME.isSelf(comMember.getInfo())) {continue;}
				comMember.getConnectHolder().send(msg);
				
				logElectionMaster.info(this.toString() + " 选主决议 向成员:" + comMember + " 发出开始投票消息.");
			}
		}
	}
	@Override
	protected void signUpHandler(RSignUpRespMsg msg) {
		CommunicationList.Info info = msg.getInfo();
		if (info != null) {
			ElectionMasterResolutionMember member = new ElectionMasterResolutionMember();
			member.setInfo(info);
			member.setResolution(this);
			member.setConnectHolder(App.ME.getCom().getConnectHolder(info));
			this.appendMember(member);
			
			log.info("[ElectionMasterResolution] signUpHandler append info:" + info.toString());
		}
	}

	
	@Override
	protected void voteEndCallback() {
		super.voteEndCallback();
		
		//	根据决议的最终结果生成主
		ElectionMasterVote vote = result();
		
		//	如果产生了主，则判断主是否是自己。从而决定是跟随还是广播自己是主
		if (vote != null) {
			MasterInfo master = new MasterInfo();
			master.setElectionId(this.id());
			master.setElectionVal(vote.getElectionVal());
			master.setInfo(vote.getInfo());
			master.setTimestamp(System.currentTimeMillis());
			App.ME.resetMaster(master);
			
			logGroup.info("[ElectionMasterResolution] 选主决议产生结论. 新主信息:" + master.getInfo() + " electionId:" + vote.getElectionId() + " electionVal:" + vote.getElectionVal());
			log.info("[ElectionMasterResolution] 选主决议产生结论. 新主信息:" + master.getInfo() + " electionId:" + vote.getElectionId() + " electionVal:" + vote.getElectionVal());
			logElectionMaster.info("[ElectionMasterResolution] 选主决议产生结论. 新主信息:" + master.getInfo() + " electionId:" + vote.getElectionId() + " electionVal:" + vote.getElectionVal());
			//	打印所有选票
			for (ElectionMasterVote v : votes) {
				logElectionMaster.info("[ElectionMasterResolution] voteEndCallback v:" + v.memberId() + " v.master:" + v.getInfo() + " v.getElectionVal:" + v.getElectionVal());
			}
			
			logElectionMaster.info("[ElectionMasterResolution] 向所有人广播主当选消息.");
			RElectedMasterMsg msg = new RElectedMasterMsg();
			msg.setInfo(App.ME.self());
			msg.setResolutionId(this.id());
			msg.setResolutionId(this.type().getVal());
			msg.setResolutionUniquenessId(this.uniquenessId());
			msg.setSeqId("RElected-" + System.currentTimeMillis());
			
			msg.setMaster(master.getInfo());
			msg.setElectionVal(msg.getElectionVal());
			msg.setTimestamp(msg.getTimestamp());
			//	向所有人广播本次选主结果
			for (CommunicationList.Info info : App.ME.getCommunicationList().allInfos()) {
				ComConnectHolder comHolder = App.ME.getCom().getConnectHolder(info);
				if (comHolder == null) {continue;}
				
				comHolder.send(msg);
				logElectionMaster.info("[ElectionMasterResolution] 向:" + info + " 发送新主当选消息");
			}
		}else {
			log.info("[ElectionMasterResolution] 本轮选主未产生结论. ");
			logElectionMaster.info("[ElectionMasterResolution] 本轮选主未产生结论. 公布所有选票.");
			for (ElectionMasterVote v : votes) {
				logElectionMaster.info("[ElectionMasterResolution] voteEndCallback v:" + v.memberId() + " v.master:" + v.getInfo());
			}
			
			//	如果一轮选主未产生结论，则重复选主决议。直到产生决议为止
			ElectionMasterResolution er = new ElectionMasterResolution();
			er.setCom(App.ME.getCom());
			er.setCommunicationList(App.ME.getCommunicationList());
			er.setSignUpInterval(GroupConfiguration.getSentryGroupResolutionElectionMasterSignupInterval());
			er.setVoteInterval(GroupConfiguration.getSentryGroupResolutionElectionMasterVoteInterval());
			
			App.ME.getResolutionPool().start(er);
		}
	}
	

	@Override
	protected ElectionMasterVote tryDecision(Collection<IResolutionMember<ElectionMasterVote>> members, Set<ElectionMasterVote> votes) {
		//	从收到的选票中找出最新的且决议值最大的选票（决议id最大的，且决议值最大）
		ElectionMasterVote vote = null;
		for (ElectionMasterVote v : votes) {
			if (vote == null) {vote = v;}
			else {
				if (v.getElectionVal().compareTo(vote.getElectionVal()) > 0) {
					vote = v;
				}
			}
		}
		return vote;
	}

}
