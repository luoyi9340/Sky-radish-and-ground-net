package org.sentry.group.com.msg_callback.server.read;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.IWaitReceiptQueue;
import org.sentry.group.com.msg.NewMemberMsg;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.com.msg.PongMsg;
import org.sentry.group.com.msg_callback.server.AServerMsgCallback;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.handly.callback.IMsgReadCallback;

import io.netty.channel.ChannelHandlerContext;


/**
 * ping 消息读callback
 * 
 * @author luoyi
 *
 */
public class PingReadCallback extends AServerMsgCallback<PingMsg> implements IMsgReadCallback<PingMsg> {

	
	static Logger logGroup = Logger.getLogger("sentry-group");
	
	
	public PingReadCallback() {super();}
	public PingReadCallback(IWaitReceiptQueue queue) {super(queue);}
	
	
	@Override
	public void callbackHandly(ChannelHandlerContext ctx, PingMsg msg) {
		//	回复Pong
		PongMsg pong = new PongMsg();
		pong.setSeqId(msg.getSeqId());
		ctx.writeAndFlush(pong);
		
		//	检测该ping来源是否在通讯清单中，如果不在则追加至通讯清单
		CommunicationList.Info info = msg.getInfo();
		if (!App.ME.getCommunicationList().exists(info)) {
			//	追加至通讯清单
			App.ME.getCommunicationList().append(info);
			//	追加至通讯器
			App.ME.getCom().append(info);
			
			log.info("[PingReadCallback] 收到新成员的ping消息，追加至通讯清单. 并向集群中其他成员广播新成员消息.");
			logGroup.info(App.ME.toString() + " [PingReadCallback] 收到新成员的ping消息，追加至通讯清单. info:" + msg.getInfo());
			System.out.println(App.ME.toString() + " [PingReadCallback] 收到新成员的ping消息，追加至通讯清单. info:" + msg.getInfo());
			
			//	向节点中其他人广播新成员
			NewMemberMsg newMemberMsg = new NewMemberMsg();
			newMemberMsg.setSeqId("NewMember-" + System.currentTimeMillis());
			newMemberMsg.setMemberInfo(msg.getInfo());
			newMemberMsg.setInfo(App.ME.self());
			newMemberMsg.setTimestamp(System.currentTimeMillis());
			for (CommunicationList.Info m : App.ME.getCommunicationList().allInfos()) {
				ComConnectHolder comHolder = App.ME.getCom().getConnectHolder(m);
				comHolder.send(newMemberMsg);
			}
		}
	}

}
