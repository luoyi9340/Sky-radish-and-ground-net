package org.sentry.group.resolution.impl.confirm_resources.demo;

import org.sentry.group.resolution.impl.confirm_resources.IConfirmResolutionFactory;


/**
 * 生成演示决议
 * 
 * @author luoyi
 *
 */
public class DemoResolutionFactory implements IConfirmResolutionFactory<DemoResolution> {

	@Override
	public DemoResolution create() {
		DemoResolution r = new DemoResolution();
		return r;
	}

	@Override
	public String resourceId() {
		return DemoResolution.RESOURCE_ID;
	}

}
