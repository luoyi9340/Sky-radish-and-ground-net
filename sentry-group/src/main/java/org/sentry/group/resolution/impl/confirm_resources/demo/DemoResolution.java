package org.sentry.group.resolution.impl.confirm_resources.demo;

import org.apache.log4j.Logger;
import org.sentry.group.resolution.ResolutionRulingTypeEnums;
import org.sentry.group.resolution.impl.confirm_resources.AConfirmResourcesResolution;
import org.sentry.group.resolution.impl.confirm_resources.ConfirmResourcesVote;


/**
 * 演示用决议
 * 
 * @author luoyi
 *
 */
public class DemoResolution extends AConfirmResourcesResolution {
	
	
	static Logger log = Logger.getLogger("sentry-resolution-confirm-resources");
	
	
	static final String RESOURCE_ID = "demo-resolution";
	
	
	/**
	 * 当检测发现本节点观测的资源失效时，投出自己的一票
	 * <p>	本节点发现了可以直接投票，不需要走网络通讯
	 * <p>	具体是否已经无效需要大家投票决定
	 * 
	 * @return
	 */
	@Override
	protected ConfirmResourcesVote confirmSelf() {
		ConfirmResourcesVote vote = ConfirmResourcesVote.buildVote(this);
		//	自己如果判断资源有效就不用投票了，既然发起投票肯定是资源无效。所以直接给false
		vote.setEffective(false);
		return vote;
	}

	
	/**
	 * 当大家投票产生结果时的回调
	 * <p>	vote可能为空。为空说明大家投票未产生结论
	 * 
	 * @param vote
	 */
	@Override
	protected void resourceConfirmCallback(ConfirmResourcesVote vote) {
		log.info("[DemoResolution] 投票产生结果:" + vote);
	}

	
	/**
	 * super里默认半数反对票就判定资源无效
	 * <p>	可以设置为一批否决/全票否决
	 * @return
	 */
	@Override
	public ResolutionRulingTypeEnums ruling() {
//		return super.ruling();
		
		return ResolutionRulingTypeEnums.HalfVote;
	}

	/**
	 * 资源唯一id
	 * <p>	同一时刻集群中只有一个唯一id的决议在执行
	 * @return
	 */
	@Override
	public String resourceId() {
		return RESOURCE_ID;
	}
	

}
