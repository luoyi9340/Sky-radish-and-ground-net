package org.sentry.group.com.msg_callback.client.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RNoticeConfirmRespMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.node.RuleEnums;
import org.sentry.group.resolution.impl.ask_master.AskMasterResolution;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通知主发起某项决议应答消息
 * 
 * @author luoyi
 *
 */
public class RNoticeConfirmRespReadCallback extends AClientMsgCallback<RNoticeConfirmRespMsg> implements IMsgReadCallback<RNoticeConfirmRespMsg>{

	
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RNoticeConfirmRespMsg msg) {
		log.info("[RNoticeConfirmRespReadCallback] 收到RNoticeConfirmRespMsg消息. isAccept:" + msg.isAccept() + " remark:" + msg.getRemark());
		
		//	如果被询问的不是主节点，则本节点退回Looking，重新发起寻主
		if (msg.isNoMaster()) {
			App.ME.setRule(RuleEnums.Looking);
			
			AskMasterResolution r = new AskMasterResolution();
			r.setCom(App.ME.getCom());
			r.setCommunicationList(App.ME.getCommunicationList());
			App.ME.getResolutionPool().start(r);
			
			log.info("[RNoticeConfirmRespReadCallback] 收到RNoticeConfirmRespMsg消息. 被询问节点不是主节点，本节点退回Looking状态，重新发起寻主.");
		}
	}
}
