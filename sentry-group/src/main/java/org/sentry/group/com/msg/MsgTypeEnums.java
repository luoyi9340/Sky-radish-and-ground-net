package org.sentry.group.com.msg;

import java.util.HashMap;
import java.util.Map;

import org.sentry.group.com.msg.resolution.RAskMasterMsg;
import org.sentry.group.com.msg.resolution.RAskMasterRespMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceMsg;
import org.sentry.group.com.msg.resolution.RConfirmResourceRespMsg;
import org.sentry.group.com.msg.resolution.RElectedMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterMsg;
import org.sentry.group.com.msg.resolution.RElectionMasterRespMsg;
import org.sentry.group.com.msg.resolution.RNoticeConfirmMsg;
import org.sentry.group.com.msg.resolution.RNoticeConfirmRespMsg;
import org.sentry.group.com.msg.resolution.RSignUpMsg;
import org.sentry.group.com.msg.resolution.RSignUpRespMsg;

/**
 * 消息类型枚举
 * 
 * @author luoyi
 *
 */
public enum MsgTypeEnums {

	
	//	心跳相关
	Ping(0x10000000, "心跳消息", PingMsg.class),
	Pong(0xF0000000, "心跳回应", PongMsg.class),
	//	新成员消息
	NewMember(0x10000002, "新成员消息", NewMemberMsg.class),
	
	
	//	决议相关
	//	决议报名
	RSignUp(0x10000001, "询问决议是否要报名消息", RSignUpMsg.class),
	RSignUpResp(0xF0000001, "决议是否报名应答", RSignUpRespMsg.class),
	//	寻主消息
	RAskMaster(0x10000003, "询问当前谁是主消息", RAskMasterMsg.class),
	RAskMasterResp(0xF0000003, "询问当前谁是主消息应答", RAskMasterRespMsg.class),
	//	主节点当选消息
	RElectedMaster(0x10000004, "主节点当选消息", RElectedMasterMsg.class),
	//	确认资源是否可用决议消息
	RConfirmResource(0x10000005, "确认资源是否可用", RConfirmResourceMsg.class),
	RConfirmResourceResp(0xF0000005, "确认资源是否可用应答", RConfirmResourceRespMsg.class),
	//	选主消息
	RElectionMaster(0x10000006, "选主消息通知", RElectionMasterMsg.class),
	RElectionMasterResp(0xF0000006, "选主消息应答", RElectionMasterRespMsg.class),
	
	//	通知主发起某项决议
	RNoticeConfirm(0x10000007, "通知主发起某项决议", RNoticeConfirmMsg.class),
	RNoticeConfirmResp(0xF0000007, "通知主发起某项决议的应答", RNoticeConfirmRespMsg.class)
	;
	private int val;
	private String desc;
	private Class<?> clazz;
	
	private MsgTypeEnums(int val, String desc, Class<?> clazz) {
		this.val = val;
		this.desc = desc;
		this.clazz = clazz;
	}

	
	static Object LOCK_GET_INSTANCE = new Object();
	static Map<Integer, MsgTypeEnums> _getInstance = null;
	public static MsgTypeEnums getInstance(int val) {
		if (_getInstance == null) {
			synchronized (LOCK_GET_INSTANCE) {
				if (_getInstance == null) {
					_getInstance = new HashMap<Integer, MsgTypeEnums>();
					for (MsgTypeEnums type : values()) {
						_getInstance.put(type.getVal(), type);
					}
				}
			}
		}
		return _getInstance.get(val);
	}
	
	
	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
}
