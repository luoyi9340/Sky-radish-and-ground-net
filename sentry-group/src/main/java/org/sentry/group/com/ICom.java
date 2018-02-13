package org.sentry.group.com;

import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.node.CommunicationList;
import org.sentry.nio.server.IServer;


/**
 * 各个哨兵间通讯器
 * 
 * @author luoyi
 *
 */
public interface ICom {

	
	/**
	 * 初始化
	 */
	void init();
	
	
	/**
	 * 根据通讯清单与其他成员建立联系
	 */
	void initTeamConnect(CommunicationList list);
	/**
	 * 取连接信息
	 */
	ComConnectHolder getConnectHolder(CommunicationList.Info info);
	/**
	 * 当前通讯清单中是否包含info
	 */
	boolean exists(CommunicationList.Info info);
	/**
	 * 追加info
	 */
	void append(CommunicationList.Info info);
	
	
	/**
	 * 追加任何消息回调
	 */
	void appendReceivedCallback(IMsgReceivedCallback callback);
	/**
	 * 触发消息回到
	 */
	void triggerReceivedCallback(CommunicationList.Info info, AGroupMsg msg);
	
	
	/**
	 * 发消息
	 */
	boolean send(CommunicationList.Info info, AGroupMsg msg);
	/**
	 * 发送带有时效的消息
	 * <p>	一定时间内没收到回执则判定消息超时
	 * <p>	回执的seqId与发出去的一致
	 */
	boolean send(CommunicationList.Info info, AGroupMsg msg, long timeout, IMsgTimeoutCallback callback);
	
	
	/**
	 * 关闭
	 */
	void close(IServer.ICloseCallback callback);
	
	
	/**
	 * 收到任何消息回调
	 */
	public static interface IMsgReceivedCallback {
		void received(CommunicationList.Info info, AGroupMsg msg);
	}
	/**
	 * 时效消息回调
	 */
	public static interface IMsgTimeoutCallback {
		void timeout(CommunicationList.Info info, AGroupMsg msg);
	}
	/**
	 * 初始化team相关回调
	 */
	public static interface IInitTeamConnectCallback {
		/**
		 * 所有人都连接失败回调
		 */
		void allError();
		/**
		 * 与成员连接成功回调
		 * <p>	ping / pong一轮成功为连接成功
		 */
		void connectSucc(CommunicationList.Info info);
	}
	
}
