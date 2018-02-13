package org.sentry.group.resolution;

import org.sentry.group.node.CommunicationList;

/**
 * 依赖网络通讯产生的选票
 * 
 * @author luoyi
 *
 */
public abstract class AComVote extends AVote {

	
	//	投票人信息
	protected CommunicationList.Info info;

	
	@Override
	public String memberId() {
		return info == null ? "unknow" : info.toString();
	}

	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("决议id:").append(this.resolutionId()).append("-")
				.append("成员id:").append(this.memberId()).append("-")
				.append("]")
				.toString();
	}
	
}
