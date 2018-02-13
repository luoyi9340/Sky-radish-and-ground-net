package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 确认资源是否可用消息
 * <p>	对方收到此消息时会在回执中确认资源是否有效
 * 
 * @author luoyi
 *
 */
public class RConfirmResourceMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RConfirmResource.getVal();
	}
	
	
	//	资源id
	protected String resourceId;


	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(super.toString())
				.append("[")
				.append("resourceId:").append(resourceId)
				.append("]")
				.toString();
	}
}
