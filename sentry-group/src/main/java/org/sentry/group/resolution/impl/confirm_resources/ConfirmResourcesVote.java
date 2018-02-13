package org.sentry.group.resolution.impl.confirm_resources;

import org.sentry.group.App;
import org.sentry.group.resolution.AComVote;


/**
 * 确认资源是可用决议选票
 * 
 * @author luoyi
 *
 */
public class ConfirmResourcesVote extends AComVote {
	
	
	//	资源是否可用
	protected boolean effective;
	
	//	资源id
	protected String resourceId;
	
	
	/**
	 * 快捷生成选票方法
	 */
	public static ConfirmResourcesVote buildVote(AConfirmResourcesResolution resolution) {
		ConfirmResourcesVote vote = new ConfirmResourcesVote();
		vote.setInfo(App.ME.self());
		vote.setResolutionId(resolution.id());
		vote.setResourceId(resolution.resourceId());
		vote.setVoteTimestamp(System.currentTimeMillis());
		return vote;
	}
	

	public boolean isEffective() {
		return effective;
	}

	public void setEffective(boolean effective) {
		this.effective = effective;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("决议id:").append(this.resolutionId()).append("-")
				.append("成员id:").append(this.memberId()).append("-")
				.append("effective:").append(effective).append("-")
				.append("]")
				.toString();
	}
	
}
