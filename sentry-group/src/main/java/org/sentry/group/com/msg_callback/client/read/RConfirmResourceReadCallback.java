package org.sentry.group.com.msg_callback.client.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RConfirmResourceMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceRespMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResourceHandler.IResultCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 确认资源是否有效消息
 * 
 * @author luoyi
 *
 */
public class RConfirmResourceReadCallback extends AClientMsgCallback<RConfirmResourceMsg> implements IMsgReadCallback<RConfirmResourceMsg>{

	
	static Logger logConfirm = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	@Override
	protected void callbackHandly(final ChannelHandlerContext ctx, final RConfirmResourceMsg msg) {
		//	从资源确认handler池中直接执行
		App.ME.getConfirmPool().handly(msg.getResourceId(), new IResultCallback() {
			public void result(boolean res) {
				//	回复对方
				RConfirmResourceRespMsg respMsg = new RConfirmResourceRespMsg();
				respMsg.setSeqId(msg.getSeqId());
				respMsg.setInfo(App.ME.self());
				respMsg.setResolutionId(msg.resolutionId());
				respMsg.setResolutionType(msg.resolutionType());
				respMsg.setResolutionUniquenessId(msg.resolutionUniquenessId());
				
				respMsg.setResourceId(msg.getResourceId());
				respMsg.setEffective(res);
				respMsg.setTimestamp(System.currentTimeMillis());
				ctx.writeAndFlush(respMsg);
				
				log.info("[RComfirmResourceReadCallback] resp to result. resourceId:" + msg.getResourceId() + " result:" + respMsg.isEffective());
				logConfirm.info("[RComfirmResourceReadCallback] resp to result. resourceId:" + msg.getResourceId() + " result:" + respMsg.isEffective());
			}
			public void noHandler() {
				//	没有handler
				log.warn("[RComfirmResourceReadCallback] no handler. resourceId:" + msg.getResourceId());
				logConfirm.info("[RComfirmResourceReadCallback] no handler. resourceId:" + msg.getResourceId());
			}
		});
	}

}
