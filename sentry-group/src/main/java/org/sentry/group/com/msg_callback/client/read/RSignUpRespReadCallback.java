package org.sentry.group.com.msg_callback.client.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.node.CommunicationList;
import org.sentry.group.resolution.IResolution;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 是否报名消息回调
 * 
 * @author luoyi
 *
 */
public class RSignUpRespReadCallback extends AClientMsgCallback<RSignUpRespMsg> implements IMsgReadCallback<RSignUpRespMsg> {

	
	static Logger log = Logger.getLogger("sentry-resolution");
	
	
	public RSignUpRespReadCallback() {super();}
	public RSignUpRespReadCallback(IWaitReceiptQueue queue, CommunicationList.Info info, ComConnectHolder holder) {super(queue, info, holder);}
	
	
	@SuppressWarnings("rawtypes")
	@Override
	public void callbackHandly(ChannelHandlerContext ctx, RSignUpRespMsg msg) {
		//	如果对方回复是‘融合到他的决议中’，则终止自己的同类型决议，同时回复给对方报名消息
		if (msg.isFusion()) {
			//	终止自己同类型决议
			IResolution resolution = App.ME.getResolutionPool().getResolution(msg);
			
			//	向对方发送消息
			try {
				if (resolution != null) {
					log.info(resolution.toString() + " 终止决议. 收到对方融合消息，融合进对方决议. msg.resolutionId:" + msg.resolutionId());
					App.ME.getResolutionPool().undo(resolution);
				}
				
				RSignUpRespMsg resp = new RSignUpRespMsg();
				resp.setInfo(App.ME.self());
				resp.setResolutionId(msg.resolutionId());
				resp.setResolutionType(msg.resolutionType());
				resp.setResolutionUniquenessId(msg.resolutionUniquenessId());
				resp.setSeqId(msg.getSeqId());
				resp.setTimestamp(System.currentTimeMillis());
				
				ctx.writeAndFlush(resp);
				
				log.info(resolution.toString() + " 自己的决议终止，想对方发送报名消息.");
			}catch (Throwable e) {
				log.error(resolution.toString() + " 融合进对方决议 向对方报名出错." + e.getMessage(), e);
			}
		}
		//	否则确认报名参加对方决议
		else {
			App.ME.getCom().triggerReceivedCallback(msg.getInfo(), msg);
		}
	}

}
