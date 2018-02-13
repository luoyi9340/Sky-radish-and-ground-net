package org.sentry.group.node;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.resolution.AComResolution;
import org.sentry.group.resolution.IResolution;
import org.sentry.group.resolution.IResolution.IDecisionListener;
import org.sentry.group.resolution.impl.confirm_resources.IConfirmResolutionFactory;

/**
 * 决议池子
 * 
 * @author luoyi
 *
 */
@SuppressWarnings("rawtypes")
public class ResolutionPool {
	
	
	static Logger log = Logger.getLogger("sentry-resolution");
	
	
	//	当前活跃的决议（key:uniquenessId value：决议对象）
	protected Map<String, IResolution> pool = new ConcurrentHashMap<String, IResolution>();
	
	//	决议生成工厂
	protected Map<String, IConfirmResolutionFactory> factoryPool = new HashMap<String, IConfirmResolutionFactory>();
	
	
	public void init() {
		//	初始化工厂池子
		Map<String, IConfirmResolutionFactory> map = App.context.getBeansOfType(IConfirmResolutionFactory.class);
		for (IConfirmResolutionFactory factory : map.values()) {
			factoryPool.put(factory.resourceId(), factory);
		}
	}
	public Map<String, IConfirmResolutionFactory> getFactoryPool() {
		return factoryPool;
	}



	/**
	 * 取决议
	 */
	public IResolution getResolution(AResolutionMsg rmsg) {
		return pool.get(rmsg.resolutionUniquenessId());
	}
	/**
	 * 开启决议
	 */
	@SuppressWarnings("unchecked")
	public void start(final IResolution resolution) {
		resolution.addDecisionListener(new IDecisionListener() {
			public void decisionFinished() {
				//	决议完成时从池子里删除
				pool.remove(resolution.uniquenessId());
				
				log.info(resolution.toString() + " 决议完成，从池子里删除. 最终结论:" + resolution.result());
			}
		});
		
		//	追加进决议池
		pool.put(resolution.uniquenessId(), resolution);
		
		log.info(resolution.toString() + " 决议开启，追加进当前决议池. ");
		//	开启决议报名
		resolution.startSignUp();
	}
	/**
	 * 终止决议
	 */
	public void undo(IResolution resolution) {
		//	终止决议
		resolution.undo();
		
		//	从池子中删除决议
		pool.remove(resolution.uniquenessId());
		
		log.info(resolution.toString() + " 决议终止，从决议池中删除.");
	}
	/**
	 * 根据唯一key终止决议
	 */
	public void undo(String uniquenessId) {
		IResolution<?> r = pool.get(uniquenessId);
		if (r != null) {undo(r);}
	}
	
	
	/**
	 * 如果收到了决议相关消息
	 */
	public void receivedMsg(AResolutionMsg rmsg) {
		IResolution resolution = pool.get(rmsg.resolutionUniquenessId());
		
		log.info("[报名消息跟踪] 决议池通过决议唯一标识符找决议对象. uniquenessId:" + rmsg.resolutionUniquenessId() + " resolution:" + resolution);
		
		if (resolution == null) {
			log.info("[ResolutionPool] 收到决议相关消息，但决议唯一标识无法找到活着的决议，忽略本次消息.");
		}else {
			if (resolution instanceof AComResolution) {
				log.info("[报名消息跟踪] 报名消息丢给决议对象处理. rmsg:" + rmsg + " resolution:" + resolution);
				
				AComResolution comResolution = (AComResolution) resolution;
				comResolution.receivedMsg(rmsg);
			}
		}
	}
}
