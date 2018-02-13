package org.sentry.group.resolution.impl.confirm_resources;


/**
 * 确认资源是否有效回调
 * <p>	用于远端成员收到确认消息（各种RConfirmResourceMsg消息）后确认自己持有资源是否有效回调
 * 
 * @author luoyi
 *
 */
public interface IConfirmResourceHandler {

	
	/**
	 * 资源id
	 * <p>	对应RConfirmResourceMsg消息中的resourceId
	 */
	String resourceId();
	
	
	/**
	 * 确认资源是否有效
	 */
	void confirm(IResultCallback result);

	
	/**
	 * 结果回调
	 * <p>	某些结果需要依赖网络通信，这里统一做成异步
	 */
	public static interface IResultCallback {
		/**
		 * 取得结果
		 */
		void result(boolean res);
		/**
		 * 没有handler
		 */
		void noHandler();
	}
}
