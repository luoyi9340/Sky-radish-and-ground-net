package org.sentry.group.resolution;

import java.util.HashMap;
import java.util.Map;

/**
 * 决议类型枚举
 * 
 * @author luoyi
 *
 */
public enum ResolutionTypeEnums {

	
	ElectionMaster(1, "election_master", "选主", true),			//	选主
	AskMaster(2, "ask_master", "询问当前谁是主", true),			//	寻主，询问当前集群谁是主
	ConfirmResource(3, "resource", "确认资源有效", false)			//	资源判定（确认资源是否有效）
	
	;
	
	private int val;
	private String code;
	private String desc;
	private boolean single;		//	同一时间只允许存在一份。如果收到了此种类型的报名消息，并且当前已经存在，则回执中让别人融入自己的决议
	
	
	private ResolutionTypeEnums(int val, String code, String desc, boolean single) {
		this.val = val;
		this.code = code;
		this.desc = desc;
		this.single = single;
	}
	
	
	static Object LOCK_GET_INSTANCE = new Object();
	static Map<Integer, ResolutionTypeEnums> _getInstance = null;
	public static ResolutionTypeEnums getInstance(int val) {
		if (_getInstance == null) {
			synchronized (LOCK_GET_INSTANCE) {
				if (_getInstance == null) {
					_getInstance = new HashMap<Integer, ResolutionTypeEnums>();
					for (ResolutionTypeEnums type : values()) {
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
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isSingle() {
		return single;
	}
	public void setSingle(boolean single) {
		this.single = single;
	}
}
