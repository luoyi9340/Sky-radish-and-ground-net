package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;


/**
 * 确认资源是否可用
 * 
 * @author luoyi
 *
 */
public class RConfirmResourceRespMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RConfirmResourceResp.getVal();
	}

	
	//	资源是否有效
	protected boolean effective;
	
	//	资源id
	protected String resourceId;

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
				.append(super.toString())
				.append("[")
				.append("resourceId:").append(resourceId)
				.append(" effective").append(effective)
				.append("]")
				.toString();
	}
}
