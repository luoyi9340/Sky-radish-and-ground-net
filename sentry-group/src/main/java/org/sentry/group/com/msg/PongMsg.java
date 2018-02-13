package org.sentry.group.com.msg;


/**
 * ping回应
 * 
 * @author luoyi
 *
 */
public class PongMsg extends org.sentry.group.com.msg.AGroupMsg{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public int type() {
		return MsgTypeEnums.Pong.getVal();
	}

	//	自己节点信息
}
