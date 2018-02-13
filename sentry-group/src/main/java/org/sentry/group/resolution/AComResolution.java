package org.sentry.group.resolution;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.sentry.group.com.ICom;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;
import org.sentry.group.node.CommunicationList;


/**
 * 需要网络通讯完成的决议
 * 
 * @author luoyi
 *
 */
public abstract class AComResolution<T extends IVote> extends AResolution<T> {

	
	static Logger log = Logger.getLogger("sentry-resolution");
	
	
	//	通讯清单
	protected CommunicationList communicationList;
	//	通讯器
	protected ICom com;
	
	
	//	已经发过邀请消息的info集合
	protected Map<String, CommunicationList.Info> tempInfoMapper = new HashMap<String, CommunicationList.Info>();
	
	@Override
	protected void signUpStartCallback() {
	}
	@Override
	protected void signUpEndCallback() {
	}

	
	/**
	 * 投票开始时向所有决议成员广播开始投票消息
	 */
	@Override
	protected void voteStartCallback() {
	}
	@Override
	protected void voteEndCallback() {
		
	}
	
	
	//	消息泛型类型
	protected Class<?> mClass = null;
	/**
	 * 收到消息
	 */
	public void receivedMsg(AResolutionMsg msg) {
		//	如果消息不是本次决议，则忽略
		if (msg.resolutionId() != this.id()
				|| msg.resolutionType() != this.type().getVal()) {
			log.info(this.toString() + " 收到投票消息，但不是本次决议，忽略.");
			return;
		}
		
		//	如果是报名消息
		if (msg instanceof RSignUpRespMsg) {
			RSignUpRespMsg signUp = (RSignUpRespMsg) msg;
			signUpHandler(signUp);
		}
		//	如果是投票消息，让子类处理
//		else if (msg.getClass().equals(mClass)) {
//			T vote = msgConvertToVote((M) msg);
//			//	找到是谁投的票
//			this.appendVote(members.get(vote.memberId()), vote);
//		}
		//	否则按照普通消息处理
		else {
			receiveHandly(msg);
		}
	}
	/**
	 * 处理投票消息
	 */
	protected abstract void receiveHandly(AResolutionMsg msg);
	/**
	 * 处理报名消息
	 */
	protected abstract void signUpHandler(RSignUpRespMsg msg);
	
	
	public CommunicationList getCommunicationList() {
		return communicationList;
	}
	public void setCommunicationList(CommunicationList communicationList) {
		this.communicationList = communicationList;
	}
	public ICom getCom() {
		return com;
	}
	public void setCom(ICom com) {
		this.com = com;
	}
}
