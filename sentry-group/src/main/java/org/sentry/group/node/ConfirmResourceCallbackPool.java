package org.sentry.group.node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sentry.group.App;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResourceHandler;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResourceHandler.IResultCallback;


/**
 * 资源确认回调池子，用于确认挂载各种资源回调，并且负责调用
 * <p>	当收到RConfirmResourceMsg时用resourceId在该池子中找是否存在resourceId对应的callback
 * <p>	若存在调用callback生成选票应答，让发起决议的人判定资源是否已经无效
 * 
 * @author luoyi
 *
 */
public class ConfirmResourceCallbackPool {

	
	//	挂载的callback
	protected List<IConfirmResourceHandler> callbacks;
	protected Map<String, IConfirmResourceHandler> callbackMapper = new HashMap<String, IConfirmResourceHandler>();
	
	
	public void init() {
		//	挂载所有的回调
		Map<String, IConfirmResourceHandler> map = App.context.getBeansOfType(IConfirmResourceHandler.class);
		for (IConfirmResourceHandler callback : map.values()) {
			callbackMapper.put(callback.resourceId(), callback);
		}
	}
	
	
	/**
	 * 根据resourceId执行回调，并生成结果
	 */
	public void handly(String resourceId, IResultCallback callback) {
		if (callbackMapper.containsKey(resourceId)) {
			callbackMapper.get(resourceId).confirm(callback);
		}else {
			callback.noHandler();
		}
	}
}
