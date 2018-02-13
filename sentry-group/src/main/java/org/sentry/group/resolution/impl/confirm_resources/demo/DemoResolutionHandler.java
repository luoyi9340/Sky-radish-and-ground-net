package org.sentry.group.resolution.impl.confirm_resources.demo;

import org.sentry.commons.utils.RandomUtils;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResourceHandler;


/**
 * 演示demo handler
 * 
 * @author luoyi
 *
 */
public class DemoResolutionHandler implements IConfirmResourceHandler {

	
	@Override
	public String resourceId() {
		return DemoResolution.RESOURCE_ID;
	}

	
	/**
	 * 当其他发现资源无效时通知自己，确认下资源是否确实无效
	 * <p>	然后给出自己的结论，到底有效无效大家投票决定
	 * <p>	统一用回调给出结果，因为某些结论同样需要异步得出，所以统一做成异步
	 * 
	 */
	@Override
	public void confirm(IResultCallback result) {
		//	确认个随机数是否 < 10
		boolean res = RandomUtils.randomElection().longValue() < 10l;
		
		result.result(res);
		//	无法确定适合投弃权票
//		result.noHandler();
	}

}
