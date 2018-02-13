package org.sentry.group.node;


/**
 * 节点角色类型枚举
 * 
 * @author luoyi
 *
 */
public enum RuleEnums {

	Looking,			//	观察节点（无主节点）
	Following,			//	跟随节点（当前有主）
	Leader				//	主节点
	
}
