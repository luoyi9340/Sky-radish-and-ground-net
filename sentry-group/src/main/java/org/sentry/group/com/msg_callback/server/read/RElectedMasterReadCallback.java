package org.sentry.group.com.msg_callback.server.read;

import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.group.node.MasterInfo;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 收到新主当选消息
 * <p>	Looking节点直接跟随新主
 * <p>	Following和Leader节点会比较新老主的决议id和决议值，
 * <p>		若新主比较高，则跟随新主。
 * <p>		否则发送回执让对方跟随老主
 * 
 * @author luoyi
 *
 */
public class RElectedMasterReadCallback extends AServerMsgCallback<RElectedMasterMsg> implements IMsgReadCallback<RElectedMasterMsg> {

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RElectedMasterMsg msg) {
		switch (App.ME.getRule()) {
		//	如果是Looking节点收到该消息，直接跟随新主
		case Looking: {
			resetMaster(msg);
			log.info("[RElectedMasterReadCallback] callbackHandly received elected msg，this is Looking, following new master. master："+ msg.getMaster());
			break;
		}
		//	如果是Following和Leader节点收到消息，则比较新老主的决议id和决议值
		case Following:
		case Leader: {
			//	比较新老主的决议id，id大的为新主
			if (App.ME.getLeaderInfo().getElectionId().longValue() > msg.resolutionId()) {
				log.info("[RElectedMasterReadCallback] callbackHandly received elected msg，this is " + App.ME.getRule() + "， crt.leader.electionId > msg.electionId，following old master. master：" + App.ME.getLeaderInfo().getInfo());
				sendElectedMaster(ctx);
			}
			//	决议id相同，比较决议值，大的为新主
			else if (App.ME.getLeaderInfo().getElectionId().longValue() == msg.resolutionId()) {
				if (App.ME.getLeaderInfo().getElectionVal().longValue() > msg.getElectionVal().longValue()) {
					log.info("[RElectedMasterReadCallback] callbackHandly received elected msg，this is " + App.ME.getRule() + "， crt.leader.electionId == msg.electionId， crt.leader.electionVal > msg.electionVal，following old master. master：" + App.ME.getLeaderInfo().getInfo());
					sendElectedMaster(ctx);
				}else {
					log.info("[RElectedMasterReadCallback] callbackHandly received elected msg， this is " + App.ME.getRule() + "， crt.leader.electionId == msg.electionId，crt.leader.electionVal <= msg.electionVal，following new master. master：" + msg.getMaster());
					resetMaster(msg);
				}
			}else {
				log.info("[RElectedMasterReadCallback] callbackHandly received elected msg， this is " + App.ME.getRule() + "， crt.leader.electionId < msg.electionId，following new master. master：" + msg.getMaster());
				resetMaster(msg);
			}
			break;
		}
		default: {
			break;
		}
	}
	}
	
	
	/**
	 * 重置自己的主
	 */
	protected void resetMaster(RElectedMasterMsg msg) {
		MasterInfo leader = new MasterInfo();
		leader.setInfo(msg.getMaster());
		leader.setElectionId(msg.resolutionId());
		leader.setElectionVal(msg.getElectionVal());
		leader.setTimestamp(msg.getTimestamp());
		
		App.ME.resetMaster(leader);
	}
	
	
	/**
	 * 回复主当选消息
	 * <p>	直接向对方发送当选消息，让对方自己‘看着办’
	 */
	protected void sendElectedMaster(ChannelHandlerContext ctx) {
		RElectedMasterMsg msg = new RElectedMasterMsg();
		msg.setMaster(App.ME.getLeaderInfo().getInfo());
		msg.setElectionVal(App.ME.getLeaderInfo().getElectionVal());
		msg.setResolutionId(App.ME.getLeaderInfo().getElectionId());
		msg.setTimestamp(App.ME.getLeaderInfo().getTimestamp());
		
		ctx.writeAndFlush(msg);
	}

}
