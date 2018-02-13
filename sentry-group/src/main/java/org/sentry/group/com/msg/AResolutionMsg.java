package org.sentry.group.com.msg;

import org.sentry.group.resolution.ResolutionTypeEnums;

/**
 * 决议相关消息
 * <p>	通过决议id与决议类型唯一标识一次决议
 * 
 * @author luoyi
 *
 */
public abstract class AResolutionMsg extends AGroupMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	//	决议id
	protected long resolutionId;
	//	决议类型
	protected int resolutionType;
	
	//	决议唯一性标识
	protected String resolutionUniquenessId;
	
	
	/**
	 * 决议类型
	 */
	public int resolutionType() {
		return resolutionType;
	}
	public void setResolutionType(int resolutionType) {
		this.resolutionType = resolutionType;
	}
	/**
	 * 决议id
	 */
	public long resolutionId() {
		return resolutionId;
	}
	public void setResolutionId(long resolutionId) {
		this.resolutionId = resolutionId;
	}
	public String resolutionUniquenessId() {
		return resolutionUniquenessId;
	}
	public void setResolutionUniquenessId(String resolutionUniquenessId) {
		this.resolutionUniquenessId = resolutionUniquenessId;
	}
	@Override
	public String toString() {
		return new StringBuilder()
					.append("[")
					.append("type:").append(MsgTypeEnums.getInstance(type())).append("-")
					.append("seqId:").append(seqId).append("-")
					.append("resolutionId:").append(resolutionId).append("-")
					.append("resolutionType:").append(ResolutionTypeEnums.getInstance(resolutionType))
					.append("]")
					.toString();
	}
	
}
