package org.sentry.group.com.msg_callback.client.write;

import org.sentry.group.com.msg_callback.client.AClientMsgCallback;
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
public class GlobalMsgWriteCallback extends AClientMsgCallback<IJsonMsg> implements IMsgWriteCallback<IJsonMsg> {

	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, IJsonMsg msg) {
		
	}

}
