package org.sentry.group.resolution.impl.confirm_resources;

import org.sentry.group.resolution.AComResolutionMember;


/**
 * 确认资源是否可用决议成员
 * 
 * @author luoyi
 *
 */
public class ConfirmResourcesResolutuonMember extends AComResolutionMember<ConfirmResourcesVote> {

	
	//	资源id（对应ConfirmResourceVote中的resourceId）
	protected String resourceId;

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
}
