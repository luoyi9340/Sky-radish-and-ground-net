package org.sentry.group.resolution.impl.confirm_resources;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.ComConnectHolder;
import org.sentry.group.com.msg.resolution.RNoticeConfirmMsg;
import org.sentry.group.node.RuleEnums;
import org.springframework.beans.factory.InitializingBean;

/**
 * 确认资源有效线程
 * 
 */
public abstract class AConfirmThread extends Thread implements InitializingBean {

	
	static Logger logGroup = Logger.getLogger("sentry-resolution");
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	//	检测是否活跃
	protected volatile boolean active;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		//	开启检测线程
		start();
	}

	
	/**
	 * 检测间隔（单位：毫秒）
	 * <p>	默认5s
	 */
	protected long interval = 5000l;
	
	@Override
	public void run() {
		active = true;
		
		while (active) {
			try {
				//	如果当前节点是Looking，不做任何事情
				if (App.ME.getRule().equals(RuleEnums.Looking)) {
					log.info("[AConfirmThread] 本节点是Looking节点，注册寻主不做任何事情，找到主再说. resourceId:" + resourceId());
					waitInterval();
					
					continue;
				}
				
				boolean res = check();
				//	如果返回false则开启投票
				if (!res) {
					//	如果自己就是主，直接开启资源确认
					if (App.ME.getRule().equals(RuleEnums.Leader)) {
						IConfirmResolutionFactory<?> factory = App.ME.getResolutionPool().getFactoryPool().get(resourceId());
						if (factory == null) {
							logGroup.info("[AConfirmThread] 检测到资源无效。且本节点就是主，但当前节点找不到 " + resourceId() + " 的决议factory. 无法发起决议投票");
							continue;
						}
						
						AConfirmResourcesResolution r = factory.create();
						r.setCom(App.ME.getCom());
						r.setCommunicationList(App.ME.getCommunicationList());
						
						App.ME.getResolutionPool().start(r);
						logGroup.info("[AConfirmThread] 检测到资源无效，已发起决议. resourceId:" + resourceId());
					}
					//	否则向主发出决议申请
					else {
						ComConnectHolder holder = App.ME.getCom().getConnectHolder(App.ME.getLeaderInfo().getInfo());
						RNoticeConfirmMsg msg = new RNoticeConfirmMsg();
						msg.setInfo(App.ME.self());
						msg.setResolutionUniquenessId(resourceId());
						msg.setSeqId("RNoticeConfirm-" + System.currentTimeMillis());
						msg.setTimestamp(System.currentTimeMillis());
						holder.send(msg);

						logGroup.info("[AConfirmThread] 检测到资源无效，向主发起决议申请. resourceId:" + resourceId());
					}
				}
				
				waitInterval();
			}catch (Throwable t) {
				log.error(t.getMessage() ,t);
				waitInterval();
			}
		}
	}
	
	/**
	 * 执行检测
	 * @return	true:检测正常, false:检测异常，需要发起投票
	 */
	protected abstract boolean check();
	/**
	 * 资源标识
	 */
	protected abstract String resourceId();
	
	
	/**
	 * 线程休眠
	 */
	protected void waitInterval() {
		synchronized (this) {
			try {
				this.wait(interval);
			} catch (InterruptedException e) {
				log.error("[AConfirmThread] 线程休眠异常 " + e.getMessage(), e);
			}
		}
	}

	
	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

}
