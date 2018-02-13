package org.sentry.group.node;

/**
 * 主节点信息
 * 
 * @author luoyi
 *
 */
public class MasterInfo {

	
	//	主信息
	protected CommunicationList.Info info;
	
	
	//	当选时的信息
	//	当前主当选时的决议id
	protected Long electionId;
	//	当前主当选时的权值
	protected Long electionVal;
	//	当选时间戳
	protected Long timestamp;
	public CommunicationList.Info getInfo() {
		return info;
	}
	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}
	public Long getElectionId() {
		return electionId;
	}
	public void setElectionId(Long electionId) {
		this.electionId = electionId;
	}
	public Long getElectionVal() {
		return electionVal;
	}
	public void setElectionVal(Long electionVal) {
		this.electionVal = electionVal;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
}
