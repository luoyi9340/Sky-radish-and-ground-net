package org.sentry.group.com.msg;

/**
 * ping消息
 * 
 * @author luoyi
 *
 */
public class PingMsg extends AGroupMsg {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public int type() {
		return MsgTypeEnums.Ping.getVal();
	}

}
