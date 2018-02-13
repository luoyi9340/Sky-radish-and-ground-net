package org.sentry.group.com.msg_callback.server.write;

import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgWriteCallback;
import org.sentry.nio.msg.json.IJsonMsg;

import io.netty.channel.ChannelHandlerContext;

/**
 * 全局消息写回调
 * <p>	仅仅为了打日志用
 * 
 * @author luoyi
 *
 */
public class GlobalMsgWriteCallback extends AServerMsgCallback<IJsonMsg> implements IMsgWriteCallback<IJsonMsg> {

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, IJsonMsg msg) {
		
	}

}
