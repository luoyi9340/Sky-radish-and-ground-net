package org.sentry.commons.guard;


/**
 * 资源检测回调
 * 
 * @author luoyi
 *
 */
public interface IDetectionCallback {

	
	/**
	 * 资源正常
	 * @param	资源命名
	 */
	void normal(String name);
	
	
	/**
	 * 资源异常
	 * @param	资源命名
	 */
	void abnormality(String name);
	
}
