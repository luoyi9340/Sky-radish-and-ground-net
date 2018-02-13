package org.sentry.group.resolution;

import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.node.CommunicationList;

/**
 * 需要网络通讯完成的决议成员
 * <p>	这里只是最终投票人在决议发起者那里的代理，实际的投票人可能在网络另一端
 * 
 * @author luoyi
 *
 * @param <T>
 */
public abstract class AComResolutionMember<T extends IVote> extends AResolutionMember<T> {

	
	//	成员信息
	protected CommunicationList.Info info;
	//	成员连接
	protected ComConnectHolder connectHolder;
	
	
	@Override
	public String id() {
		return info.toString();
	}


	public CommunicationList.Info getInfo() {
		return info;
	}
	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}
	public ComConnectHolder getConnectHolder() {
		return connectHolder;
	}
	public void setConnectHolder(ComConnectHolder connectHolder) {
		this.connectHolder = connectHolder;
	}


	@Override
	public String toString() {
		return new StringBuilder()
				.append("[")
				.append("决议id:").append(this.resolution().id())
				.append("info:").append(this.info.toString())
				.append("]")
				.toString();
	}
	
}
