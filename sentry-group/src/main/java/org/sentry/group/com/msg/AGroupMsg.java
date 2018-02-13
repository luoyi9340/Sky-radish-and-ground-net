package org.sentry.group.com.msg;

import org.sentry.group.node.CommunicationList;
import org.sentry.nio.msg.json.IJsonMsg;

/**
 * 哨兵组来往消息
 * 
 * @author luoyi
 *
 */
public abstract class AGroupMsg implements IJsonMsg {
	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;

	
	//	消息流水号
	protected String seqId;
	
	//	消息时间戳
	protected long timestamp;
	
	//	本节点信息
	protected CommunicationList.Info info;

	public String getSeqId() {
		return seqId;
	}

	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public CommunicationList.Info getInfo() {
		return info;
	}

	public void setInfo(CommunicationList.Info info) {
		this.info = info;
	}

	@Override
	public int hashCode() {
		return (type() + ":" + seqId).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!this.getClass().equals(obj.getClass())) {return false;}

		AGroupMsg msg = (AGroupMsg)obj;
		if (this.getSeqId() == null || msg.getSeqId() == null) {return false;}
		
		return this.type() == msg.type()
				&& this.getSeqId().equals(msg.getSeqId());
	}

	@Override
	public String toString() {
		return new StringBuilder()
					.append("[")
					.append("type:").append(MsgTypeEnums.getInstance(type())).append("-")
					.append("seqId:").append(seqId)
					.append("]")
					.toString();
	}
	
}
