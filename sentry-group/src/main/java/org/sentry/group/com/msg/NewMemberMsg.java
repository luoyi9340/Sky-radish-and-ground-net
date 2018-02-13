package org.sentry.group.com.msg;

import org.sentry.group.node.CommunicationList;

/**
 * 新成员信息
 * <p>	该信息不需要回执
 * 
 * @author luoyi
 *
 */
public class NewMemberMsg extends AGroupMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1l;
	
	
	@Override
	public int type() {
		return MsgTypeEnums.NewMember.getVal();
	}
	
	
	//	新成员信息
	protected CommunicationList.Info memberInfo;


	public CommunicationList.Info getMemberInfo() {
		return memberInfo;
	}

	public void setMemberInfo(CommunicationList.Info memberInfo) {
		this.memberInfo = memberInfo;
	}

}
