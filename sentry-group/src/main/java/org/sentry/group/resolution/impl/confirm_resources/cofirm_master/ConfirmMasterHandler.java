package org.sentry.group.resolution.impl.confirm_resources.cofirm_master;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.ICom;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.com.msg.PingMsg;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.node.CommunicationList.Info;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResourceHandler;


/**
 * 决议成员端确认master是否有效handler
 * 
 * @author luoyi
 *
 */
public class ConfirmMasterHandler implements IConfirmResourceHandler {

	
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	@Override
	public String resourceId() {
		return ConfirmMasterResolution.RESOURCE_ID;
	}

	
	@Override
	public void confirm(final IResultCallback result) {
		switch (App.ME.getRule()) {
		//	如果自己就是leader，则直接回复‘我还活着’
		case Leader: {
			log.info("[ConfirmMasterHandler] 确认主节点是否挂掉: 我就是leader节点，我还活着.");
			result.result(true);
			return;
		}
		//	如果是Following节点，则向master发一条ping
		case Following: {
			ComConnectHolder holder = App.ME.getCom().getConnectHolder(App.ME.getLeaderInfo().getInfo());
			
			//	向当前master发ping，确认master是否存活
			PingMsg ping = new PingMsg();
			boolean res = holder.send(ping, GroupConfiguration.getSocketTimeout(), 
					new ICom.IMsgTimeoutCallback() {
						public void timeout(Info info, AGroupMsg msg) {
							log.info("[ConfirmMasterHandler] 确认主节点是否挂掉: 给当前leader发ping超时，当前leader已经挂了. ");
							
							//	ping超时判定master已死亡
							result.result(false);
						}
					},
					new ICom.IMsgReceivedCallback() {
						public void received(Info info, AGroupMsg msg) {
							log.info("[ConfirmMasterHandler] 确认主节点是否挂掉: 给当前leader发ping消息成功，当前leader还活着. ");
							
							//	平收到成功回调才判定master存活
							result.result(true);
						}
					});
			if (!res) {
				//	消息发送失败也判定master已经死亡
				log.info("[ConfirmMasterHandler] 确认主节点是否挂掉: 给当前leader发ping消息失败，当前leader已经挂了. ");
				
				result.result(false);
			}
			
			return;
		}
		//	如果是Looking节点不做任何事情
		case Looking: {
			log.info("[ConfirmMasterHandler] 确认主节点是否挂掉: 我是Looking节点，leader我还在找呢. ");
		}
		default: {}
		}
	}

}
