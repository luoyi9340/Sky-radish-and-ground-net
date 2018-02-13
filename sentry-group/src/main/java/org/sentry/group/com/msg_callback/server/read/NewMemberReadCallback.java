package org.sentry.group.com.msg_callback.server.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.NewMemberMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * 收到新成员消息
 * 
 * @author luoyi
 *
 */
public class NewMemberReadCallback extends AServerMsgCallback<NewMemberMsg> implements IMsgReadCallback<NewMemberMsg> {

	
	static Logger logGroup = Logger.getLogger("sentry-group");
	
	
	@Override
	protected void callbackHandly(ChannelHandlerContext ctx, NewMemberMsg msg) {
		//	检测新成员是否在通讯清单中
		if (App.ME.getCommunicationList().exists(msg.getMemberInfo())) {
			return;
		}
		
		//	否则将新成员信息追加至通讯清单，通讯器
		App.ME.getCommunicationList().append(msg.getMemberInfo());
		App.ME.getCom().append(msg.getMemberInfo());
		
		log.info("[NewMemberReadCallback] 收到新加入成员消息,追加至通讯清单. info:" + msg.getInfo());
		logGroup.info(App.ME.toString() + " [NewMemberReadCallback] 收到新成员的ping消息，追加至通讯清单. info:" + msg.getInfo());
	}

}
