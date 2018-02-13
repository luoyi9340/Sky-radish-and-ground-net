package org.sentry.group.resolution.impl.confirm_resources.cofirm_master;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.resolution.impl.confirm_resources.AConfirmResourcesResolution;
import org.sentry.group.resolution.impl.confirm_resources.ConfirmResourcesVote;
import org.sentry.group.resolution.impl.election_master.ElectionMasterResolution;


/**
 * 确认主是否有效决议
 * 
 * @author luoyi
 *
 */
public class ConfirmMasterResolution extends AConfirmResourcesResolution {

	
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	static Logger logGroup = Logger.getLogger("sentry-group");
	
	
	//	确认主是否有效resourceId
	public final static String RESOURCE_ID = "confirm_master";
	
	
	@Override
	protected ConfirmResourcesVote confirmSelf() {
		//	自己发起的决议，先投一票反对票
		ConfirmResourcesVote vote = new ConfirmResourcesVote();
		vote.setResolutionId(this.id());
		vote.setInfo(App.ME.self());
		
		vote.setResourceId(resourceId());
		vote.setEffective(false);
		vote.setVoteTimestamp(System.currentTimeMillis());
		return vote;
	}

	@Override
	protected void resourceConfirmCallback(ConfirmResourcesVote vote) {
		//	如果结论是资源已经无效，则重新发起选主决议
		log.info("[ConfirmMasterResolution] 确认主资源是否有效，产生决议"
								+ " vote.resolutionId:" + vote.resolutionId() 
								+ " vote.isEffective:" + vote.isEffective()
								+ " vote.getResourceId:" + vote.getResourceId());
		if (!vote.isEffective()) {
			logGroup.info("[ConfirmMasterResolution] 确认主节点已经挂掉，开启选主决议.");
		}else {
			logGroup.info("[ConfirmMasterResolution] 确认主节点没挂，刚刚那货眼花了.");
		}
		
		
		//	如果最终结论是主无效，则重新开始选主
		if (!vote.isEffective()) {
			ElectionMasterResolution er = new ElectionMasterResolution();
			er.setCom(App.ME.getCom());
			er.setCommunicationList(App.ME.getCommunicationList());
			er.setSignUpInterval(GroupConfiguration.getSentryGroupResolutionElectionMasterSignupInterval());
			er.setVoteInterval(GroupConfiguration.getSentryGroupResolutionElectionMasterVoteInterval());
			
			App.ME.getResolutionPool().start(er);
		}
	}

	@Override
	protected String resourceId() {
		return RESOURCE_ID;
	}

}
