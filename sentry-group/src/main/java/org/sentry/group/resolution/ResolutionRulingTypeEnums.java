package org.sentry.group.resolution;

import java.util.HashMap;
import java.util.Map;

/**
 * 决议裁决类型
 * 
 * @author luoyi
 *
 */
public enum ResolutionRulingTypeEnums {

	
	OneVote(1, "one", "一票否决"),
	HalfVote(2, "half", "半数以上否决"),
	AllVote(3, "all", "全票否决"),
	Every(4, "every", "每次投票迭代产生")
	
	;
	
	
	private int val;
	private String code;
	private String desc;
	
	
	private static Object LOCK_GET_INSTANCE = new Object();
	private static Map<Integer, ResolutionRulingTypeEnums> _getInstance = null;
	public static ResolutionRulingTypeEnums getInstance(int val) {
		if (_getInstance == null) {
			synchronized (LOCK_GET_INSTANCE) {
				if (_getInstance == null) {
					_getInstance = new HashMap<Integer, ResolutionRulingTypeEnums>();
					for (ResolutionRulingTypeEnums type : values()) {
						_getInstance.put(type.getVal(), type);
					}
				}
			}
		}
		return _getInstance.get(val);
	}
	
	
	private ResolutionRulingTypeEnums(int val, String code, String desc) {
		this.val = val;
		this.code = code;
		this.desc = desc;
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
}
