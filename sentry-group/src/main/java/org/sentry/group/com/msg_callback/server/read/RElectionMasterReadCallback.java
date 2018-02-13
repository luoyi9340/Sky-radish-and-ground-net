package org.sentry.group.com.msg_callback.server.read;

import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterRespMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.group.node.RuleEnums;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 选主消息read callback
 * 
 * @author luoyi
 *
 */
public class RElectionMasterReadCallback extends AServerMsgCallback<RElectionMasterMsg> implements IMsgReadCallback<RElectionMasterMsg> {

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RElectionMasterMsg msg) {
		switch (App.ME.getRule()) {
		//	Looking节点直接回复选票
		case Looking: {
			RElectionMasterRespMsg resp = new RElectionMasterRespMsg();
			resp.setInfo(App.ME.self());
			resp.setResolutionId(msg.resolutionId());
			resp.setResolutionType(msg.resolutionType());
			resp.setResolutionUniquenessId(msg.resolutionUniquenessId());
			resp.setSeqId(msg.getSeqId());
			
			resp.setElectionVal(RandomUtils.randomElection());
			resp.setTimestamp(System.currentTimeMillis());
			ctx.writeAndFlush(resp);
			
			log.info("[RElectionMasterReadCallback] callbackHandly received election msg. this is Looking, participate in. ekection.val:" + resp.getElectionVal());
		}
		case Following:
		case Leader:{
			//	Following和Leader节点检测，检测当前的决议id是否大于主现在当选时的决议id
			//	如果大于，则退回Looking节点，投出选票
			if (msg.resolutionId() > App.ME.getLeaderInfo().getElectionVal().longValue()) {
				App.ME.setRule(RuleEnums.Looking);
				
				RElectionMasterRespMsg resp = new RElectionMasterRespMsg();
				resp.setResolutionId(msg.resolutionId());
				resp.setInfo(App.ME.self());
				resp.setResolutionType(msg.resolutionType());
				resp.setSeqId(msg.getSeqId());
				resp.setTimestamp(System.currentTimeMillis());
				resp.setElectionVal(RandomUtils.randomElection());

				ctx.writeAndFlush(resp);

				log.info("[RElectionMasterReadCallback] callbackHandly received election msg. this is Following/Leader but this.electionId < msg.resolution.id, participate in. ekection.val:" + resp.getElectionVal());
			}
			//	否则向对方发送当前主的当选消息
			else {
				RElectedMasterMsg resp = new RElectedMasterMsg();
				resp.setInfo(App.ME.self());
				resp.setMaster(App.ME.getLeaderInfo().getInfo());
				resp.setResolutionId(App.ME.getLeaderInfo().getElectionId());
				resp.setElectionVal(App.ME.getLeaderInfo().getElectionVal());
				resp.setTimestamp(System.currentTimeMillis());
				resp.setSeqId(msg.getSeqId());
				
				ctx.writeAndFlush(resp);
				
				log.info("[RElectionMasterReadCallback] callbackHandly received election msg. this is Following/Leader but this.electionId >= msg.resolution.id, response to elected msg. master.resolution.id:" + resp.resolutionId());
			}
		}
		default: {}
	}
		
	}

}
