package org.sentry.group.com.msg_callback.client.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RNoticeConfirmMsg;
import org.sentry.group.com.msg.resolution.RNoticeConfirmRespMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.node.RuleEnums;
import org.sentry.group.resolution.IResolution;
import org.sentry.group.resolution.impl.confirm_resources.AConfirmResourcesResolution;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResolutionFactory;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;

/**
 * 通知主发起某项决议消息
 * 
 * @author luoyi
 *
 */
public class RNoticeConfirmReadCallback extends AClientMsgCallback<RNoticeConfirmMsg> implements IMsgReadCallback<RNoticeConfirmMsg>{

	
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RNoticeConfirmMsg msg) {
		//	收到此消息时如果当前节点不是主，则返回noMaster。让对方重新寻主
		if (!App.ME.getRule().equals(RuleEnums.Leader)) {
			RNoticeConfirmRespMsg resp = new RNoticeConfirmRespMsg();
			resp.setInfo(App.ME.self());
			resp.setAccept(false);
			resp.setNoMaster(true);
			resp.setRemark("当前节点不是主节点");
			ctx.writeAndFlush(resp);
			
			log.info("[RNoticeConfirmReadCallback] 收到RNoticeConfirmMsg消息，但本节点不是主节点，不予受理. resolutionUniquenessId:" + msg.resolutionUniquenessId() + " info:" + msg.getInfo());
			return;
		}
		
		//	先判断当前有没有针对此资源的确认决议，如果没有则重新发起
		IResolution<?> resolution = App.ME.getResolutionPool().getResolution(msg);
		if (resolution == null) {
			IConfirmResolutionFactory factory = App.ME.getResolutionPool().getFactoryPool().get(msg.resolutionUniquenessId());
			if (factory == null) {
				RNoticeConfirmRespMsg resp = new RNoticeConfirmRespMsg();
				resp.setInfo(App.ME.self());
				resp.setAccept(false);
				resp.setNoMaster(false);
				resp.setRemark("主节点不包含类型为:" + msg.resolutionUniquenessId() + " 的决议");
				ctx.writeAndFlush(resp);
				
				log.info("[RNoticeConfirmReadCallback] 收到RNoticeConfirmMsg消息，但不包含此类型的决议，不予受理. resolutionUniquenessId:" + msg.resolutionUniquenessId()+ " info:" + msg.getInfo());
				return;
			}
			
			AConfirmResourcesResolution r = factory.create();
			r.setCom(App.ME.getCom());
			r.setCommunicationList(App.ME.getCommunicationList());
			App.ME.getResolutionPool().start(r);
			
			RNoticeConfirmRespMsg resp = new RNoticeConfirmRespMsg();
			resp.setInfo(App.ME.self());
			resp.setAccept(true);
			resp.setNoMaster(false);
			resp.setRemark("主节点已受理");
			ctx.writeAndFlush(resp);
			
			log.info("[RNoticeConfirmReadCallback] 收到RNoticeConfirmMsg消息，主节点已受理，决议开启. resolutionUniquenessId:" + msg.resolutionUniquenessId()+ " info:" + msg.getInfo());
			
			return;
		}
	}
}
