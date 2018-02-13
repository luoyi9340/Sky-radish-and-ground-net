package org.sentry.group.com.msg_callback.client.read;

import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.resolution.RAskMasterRespMsg;
import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 询问主消息回复
 * 
 * @author luoyi
 *
 */
public class RAskMasterRespReadCallback extends AClientMsgCallback<RAskMasterRespMsg> implements IMsgReadCallback<RAskMasterRespMsg> {

	
	public RAskMasterRespReadCallback() {super();}
	public RAskMasterRespReadCallback(IWaitReceiptQueue queue, CommunicationList.Info info, ComConnectHolder holder) {super(queue, info, holder);}
	
	
	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RAskMasterRespMsg msg) {
		log.info("[RAskMasterRespCallback] received vote. seqId:" + msg.getSeqId()
									+ " master:" + msg.getMaster()
									+ " msg.resolutionId:" + msg.resolutionId()
									+ " msg.getElectionId:" + msg.getElectionId()
									+ " msg.getElectionVal:" + msg.getElectionVal());
		
		//	处理选票
		App.ME.getCom().triggerReceivedCallback(msg.getInfo(), msg);
	}

}
