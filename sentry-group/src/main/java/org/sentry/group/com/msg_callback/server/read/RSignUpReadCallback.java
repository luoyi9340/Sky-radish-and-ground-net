package org.sentry.group.com.msg_callback.server.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.group.node.ResolutionPool;
import org.sentry.group.resolution.IResolution;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 决议报名消息
 * 
 * @author luoyi
 *
 */
public class RSignUpReadCallback extends AServerMsgCallback<RSignUpMsg> implements IMsgReadCallback<RSignUpMsg> {

	
	static Logger log = Logger.getLogger("sentry-resolution");
	
	
	public RSignUpReadCallback() {super();}
	public RSignUpReadCallback(IWaitReceiptQueue queue) {super(queue);}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RSignUpMsg msg) {
		//	如果当前存在同一类型唯一决议，则根据对方开启报名时间戳来判断是否融入对方
		ResolutionPool resolutionPool = App.ME.getResolutionPool();
		IResolution resolution = resolutionPool.getResolution(msg);
		if (resolution != null) {
			//	若对方的时间戳早于自己，则融入对方，取消掉自己的决议
			if (msg.getTimestamp() < resolution.startSignUpTimestamp()) {
				//	撤销自己的决议
				App.ME.getResolutionPool().undo(resolution);
				log.info(resolution.toString() + " 终止决议. 收到对方报名通知, 己方决议时间 > 对方决议时间 融入对方. msg.resolutionId:" + msg.resolutionId());
				//	向对方报名
				receivedSignUp(ctx, msg);
			}
			//	若对方的时间戳比自己晚，则发送给对方融合消息
			else {
				RSignUpRespMsg resp = new RSignUpRespMsg();
				resp.setResolutionId(resolution.id());
				resp.setResolutionType(resolution.type().getVal());
				resp.setResolutionUniquenessId(resolution.uniquenessId());
				resp.setInfo(App.ME.self());
				resp.setSeqId(msg.getSeqId());
				resp.setFusion(true);
				resp.setFusionId(resolution.id());
				ctx.writeAndFlush(resp);
			
				log.info("[RSignUpReadCallback]  收到对方报名通知, 己方决议时间 <= 对方决议时间.timestamp，向对方发送融合消息. ");
			}
		}else {
			receivedSignUp(ctx, msg);
			
			log.info("[RSignUpReadCallback] 收到对方报名通知, 正常报名对方. ");
		}
	}
	
	
	/**
	 * 回复报名消息
	 */
	protected void receivedSignUp(ChannelHandlerContext ctx, RSignUpMsg msg) {
		//	报名参加决议（目前所有节点角色一致），回写报名消息
		RSignUpRespMsg resp = new RSignUpRespMsg();
		resp.setResolutionId(msg.resolutionId());
		resp.setResolutionType(msg.resolutionType());
		resp.setResolutionUniquenessId(msg.resolutionUniquenessId());
		resp.setInfo(App.ME.self());
		resp.setSeqId(msg.getSeqId());
		ctx.writeAndFlush(resp);
	}

}
