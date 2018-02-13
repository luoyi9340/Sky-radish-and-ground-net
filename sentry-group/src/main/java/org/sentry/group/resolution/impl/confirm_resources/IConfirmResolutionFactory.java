package org.sentry.group.resolution.impl.confirm_resources;

/**
 * 决议生成工厂
 * 
 * @author luoyi
 *
 */
public interface IConfirmResolutionFactory<T extends AConfirmResourcesResolution> {

	
	/**
	 * 生成决议
	 */
	T create();
	
	
	/**
	 * 决议唯一标识
	 */
	String resourceId();
}
