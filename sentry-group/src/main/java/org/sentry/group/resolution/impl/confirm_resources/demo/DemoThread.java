package org.sentry.group.resolution.impl.confirm_resources.demo;

import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.resolution.impl.confirm_resources.AConfirmThread;


/**
 * 演示检测线程
 * @author luoyi
 *
 */
public class DemoThread extends AConfirmThread {

	@Override
	protected boolean check() {
		return !(RandomUtils.randomElection() < 10l);
	}

	@Override
	protected String resourceId() {
		return DemoResolution.RESOURCE_ID;
	}

}
