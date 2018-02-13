package org.sentry.group.com.msg_callback.server.read;

import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RElectionMasterRespMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 选主消息应答read callback
 * 
 * @author luoyi
 *
 */
public class RElectionMasterRespReadCallback extends AServerMsgCallback<RElectionMasterRespMsg> implements IMsgReadCallback<RElectionMasterRespMsg> {

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, RElectionMasterRespMsg msg) {
		App.ME.getCom().triggerReceivedCallback(msg.getInfo(), msg);
	}

}
