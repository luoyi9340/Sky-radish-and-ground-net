package org.sentry.group.com.msg_callback.server.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.resolution.RConfirmResourceRespMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 确认资源是否有效消息
 * 
 * @author luoyi
 *
 */
public class RConfirmResourceRespReadCallback extends AServerMsgCallback<RConfirmResourceRespMsg> implements IMsgReadCallback<RConfirmResourceRespMsg>{

	
	static Logger logConfirm = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	@Override
	protected void callbackHandly(final ChannelHandlerContext ctx, final RConfirmResourceRespMsg msg) {
		logConfirm.info("[RConfirmResourceRespReadCallback] 收到确认资源消息. info:" + msg.getInfo() + " msg.resourceId:" + msg.getResourceId() + " msg.isEffective:" + msg.isEffective());
		
		App.ME.getCom().triggerReceivedCallback(msg.getInfo(), msg);
	}
}
