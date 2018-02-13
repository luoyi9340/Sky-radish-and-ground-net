package org.sentry.group.com.msg.resolution;

import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.MsgTypeEnums;

/**
 * 决议报名消息
 * <p>	现在要发起一项决议，询问别人要不要报名参加
 * 
 * @author luoyi
 *
 */
public class RSignUpRespMsg extends AResolutionMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int type() {
		return MsgTypeEnums.RSignUpResp.getVal();
	}
	
	//	是否让对方融入自己的决议中
	protected boolean fusion = false;
	//	融合决议的id
	protected Long fusionId;

	public boolean isFusion() {
		return fusion;
	}

	public void setFusion(boolean fusion) {
		this.fusion = fusion;
	}

	public Long getFusionId() {
		return fusionId;
	}

	public void setFusionId(Long fusionId) {
		this.fusionId = fusionId;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append(super.toString())
				.append("[").append("fusion:").append(fusion).append(" fusionId:").append(fusionId).append("]")
				.toString();
	}
}
