package org.sentry.group.com.msg_callback.server.read;

import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.App;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.resolution.RAskMasterMsg;
import org.sentry.group.com.msg.resolution.RAskMasterRespMsg;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;

/**
 * 询问主消息callback
 * 
 * @author luoyi
 *
 */
public class RAskMasterReadCallback extends AServerMsgCallback<RAskMasterMsg> implements IMsgReadCallback<RAskMasterMsg> {

	
	public RAskMasterReadCallback() {super();}
	public RAskMasterReadCallback(IWaitReceiptQueue queue) {super(queue);}
	
	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RAskMasterMsg msg) {
		switch (App.ME.getRule()) {
		//	如果当前是Looking状态，则回复自己的选主信息
		case Looking: {
			RAskMasterRespMsg resp = new RAskMasterRespMsg();
			resp.setInfo(App.ME.self());
			resp.setElectionId(-1l);
			resp.setElectionVal(RandomUtils.randomElection());
			resp.setMaster(App.ME.self());
			resp.setResolutionId(msg.resolutionId());
			resp.setResolutionType(msg.resolutionType());
			resp.setResolutionUniquenessId(msg.resolutionUniquenessId());
			resp.setSeqId(msg.getSeqId());
			
			ctx.writeAndFlush(resp);
			
			log.info("[RAskMasterCallback] 收到对方寻主消息 本节点是looking节点，回复自己的参选信息. resp.getElectionVal:" + resp.getElectionVal() + " resp.getElectionId:" + resp.getElectionId());
			return;
		}
		//	如果节点处于跟随状态，则回复当前主当选信息
		case Following: {
			RElectedMasterMsg resp = new RElectedMasterMsg();
			resp.setSeqId(msg.getSeqId());
			resp.setInfo(App.ME.self());
			resp.setResolutionId(App.ME.getLeaderInfo().getElectionId());
			resp.setElectionVal(App.ME.getLeaderInfo().getElectionVal());
			resp.setMaster(App.ME.getLeaderInfo().getInfo());
			resp.setTimestamp(App.ME.getLeaderInfo().getTimestamp());
			
			ctx.writeAndFlush(resp);
			
			log.info("[RAskMasterCallback] 收到对方寻主消息 本节点是Following节点，回复当前主当选时的信息. resp.getElectionVal:" + resp.getElectionVal() + " resp.getElectionId:" + resp.resolutionId());
			return;
		}
		//	如果节点就是主节点，则回复自己（信息都在ME.leaderInfo中，ME.leaderInfo.info应该与ME.self一致）
		case Leader: {
			RElectedMasterMsg resp = new RElectedMasterMsg();
			resp.setSeqId(msg.getSeqId());
			resp.setInfo(App.ME.self());
			resp.setResolutionId(App.ME.getLeaderInfo().getElectionId());
			resp.setElectionVal(App.ME.getLeaderInfo().getElectionVal());
			resp.setMaster(App.ME.getLeaderInfo().getInfo());
			resp.setTimestamp(App.ME.getLeaderInfo().getTimestamp());
			
			ctx.writeAndFlush(resp);
			
			log.info("[RAskMasterCallback] 收到对方寻主消息 本节点是Leader节点，回复自己当选时的信息. resp.getElectionVal:" + resp.getElectionVal() + " resp.getElectionId:" + resp.resolutionId());
			return;
		}
		default: {break;}
		}
	}

}
