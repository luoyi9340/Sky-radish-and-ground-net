package org.sentry.commons.utils;

import java.util.HashMap;
import java.util.Map;


/**
 * 状态清单
 * <p>	负责记录每一个资源是否有效
 * 
 * @author luoyi
 *
 */
public class StatusRollCall {

	
	//	清单（key：db名称，properties里配的，value：是否存活）
	public Map<String, Boolean> listsMapper = new HashMap<String, Boolean>();
	
	
	//	当前使用的是哪一个
	protected String currentName;
	
	
	/**
	 * 取另一个有效的
	 * <p>	除了currentName以外
	 * 
	 * @return	null：已经取不到有效的了 | 非空：取到一个正常的
	 */
	public String another() {
		//	如果清单长度就是1，则判定为取不到了
		if (listsMapper.size() == 1) {return null;}
		
		String res = null;
		for (Map.Entry<String, Boolean> entry : listsMapper.entrySet()) {
			if (entry.getValue().equals(Boolean.TRUE) 
					&& !entry.getKey().equals(currentName)) {
				res = entry.getKey();
			}
		}
		return res;
	}
	
	
	/**
	 * 检测资源是否有效
	 */
	public boolean isActive(String name) {
		return listsMapper.containsKey(name) 
				&& listsMapper.get(name).equals(Boolean.TRUE);
	}
	/**
	 * 重置清单状态
	 */
	public void status(String name, Boolean status) {
		listsMapper.put(name, status);
	}


	public String getCurrentName() {
		return currentName;
	}


	public void setCurrentName(String currentName) {
		this.currentName = currentName;
	}
}
