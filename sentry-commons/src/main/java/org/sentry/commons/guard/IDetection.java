package org.sentry.commons.guard;


/**
 * 检测资源是否可用
 * 
 * @author luoyi
 *
 */
public interface IDetection {
	
	
	/**
	 * 资源命名
	 */
	String name();

	
	/**
	 * 探测资源是否可用
	 * 
	 * @return	true | false
	 */
	boolean detection();
	
	
}
