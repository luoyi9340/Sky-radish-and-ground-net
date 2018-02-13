package org.sentry.group.node;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.sentry.commons.guard.Sentry;
import org.sentry.group.App;
import org.sentry.group.com.ICom;
import org.sentry.group.com.msg.AGroupMsg;
import org.sentry.group.com.msg.AResolutionMsg;
import org.sentry.group.commons.GroupConfiguration;
import org.sentry.group.node.CommunicationList.Info;
import org.sentry.group.resolution.impl.ask_master.AskMasterResolution;
import org.sentry.group.resolution.impl.election_master.ElectionMasterResolution;
import org.sentry.nio.server.IServer;

/**
 * 节点信息
 * 
 * @author luoyi
 *
 */
public class TeamMembers extends Sentry {
	
	
	static Logger log = Logger.getLogger("sentry-group");
	static Logger logResolution = Logger.getLogger("sentry-resolution");

	
	//	节点ip
	protected String host;
	//	节点端口号
	protected int port;
	
	//	节点状态
	protected RuleEnums rule;

	//	主信息
	protected MasterInfo leaderInfo;

	
	//	通讯器
	protected ICom com;
	//	通讯清单
	protected CommunicationList communicationList;
	//	本节点信息
	protected CommunicationList.Info selfInfo;
	
	//	决议池
	protected ResolutionPool resolutionPool;
	//	资源确认handler池
	protected ConfirmResourceCallbackPool confirmPool;
	
	
	public void startWorking() {
		linkedTeam();
	}
	/**
	 * 初始化
	 */
	public void init() {
		//	初始化时都是Looking状态
		rule = RuleEnums.Looking;
		try {
			this.host = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(this.toString() + " init error. " + e.getMessage(), e);
		}
		this.port = GroupConfiguration.getPort();
		
		log.info(this.toString() + " 初始化本节点信息成功.");
	}
	/**
	 * 与其他人建立连接
	 */
	protected void linkedTeam() {
		//	初始化任何消息回调
		com.appendReceivedCallback(new ICom.IMsgReceivedCallback() {
			public void received(Info info, AGroupMsg msg) {
				//	如果是决议消息
				if (msg instanceof AResolutionMsg) {
					AResolutionMsg rmsg = (AResolutionMsg) msg;
					
					logResolution.info("[报名消息跟踪] 通讯器回调将消息丢给决议池. rmsg:" + rmsg + " info:" + info);
					
					resolutionPool.receivedMsg(rmsg);
				}
			}
		});
		//	与其他人建立连接
		com.initTeamConnect(communicationList);
		
		//	不管连接建立成功与否，2s后均开启寻主决议
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.error(this.toString() + " linkedTeam error. " + e.getMessage(), e);
		}
		
		AskMasterResolution resolution = new AskMasterResolution();
		resolution.setCommunicationList(communicationList);
		resolution.setCom(com);
		resolution.setSignUpInterval(GroupConfiguration.getSentryGroupResolutionAskMasterSignupInterval());
		resolution.setVoteInterval(GroupConfiguration.getSentryGroupResolutionAskMasterVoteInterval());
		resolutionPool.start(resolution);
		
		log.info(this.toString() + " 初始化通讯器，开启通讯器成功.");
	}
	/**
	 * 自己的info信息
	 */
	public Info self() {
		if (selfInfo == null) {
			selfInfo = new Info(App.ME.getHost(), App.ME.getPort());
		}
		return selfInfo;
	}
	/**
	 * 检测info是否是本节点信息
	 */
	public boolean isSelf(CommunicationList.Info info) {
		//	如果host和port都是自己则不予连接
		if (("127.0.0.1".equals(info.getHost())) || "localhost".equalsIgnoreCase(info.getHost()) || (this.getHost().equals(info.getHost()))
				&& this.getPort() == info.getPort()) {
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * 重置主
	 */
	public void resetMaster(MasterInfo master) {
		//	如果是自己当选了，则更新自己状态
		if (master.getInfo().equals(self())) {
			log.info(this.toString() + " 重置主信息，当前主就是本节点，状态置为Leader! 新主:" + master.getInfo());
			
			//	对第一次寻主产生的主，electionId置为0。大于剩下的新节点的electionId
			if (master.electionId.longValue() == -1) {
				master.electionId = 0l;
			}
			
			this.rule = RuleEnums.Leader;
			this.leaderInfo = master;
		}
		//	否则自己的状态改为Following
		else {
			log.info(this.toString() + " 重置主信息，当前主不是本节点，状态置为Following. 新主:" + master.getInfo());
			
			//	对第一次寻主产生的主，electionId置为0。大于剩下的新节点的electionId
			if (master.electionId.longValue() == -1) {
				master.electionId = 0l;
			}
			
			this.rule = RuleEnums.Following;
			this.leaderInfo = master;
		}
		
		
		//	如果当前存在寻主或选主决议，直接终止
		resolutionPool.undo(AskMasterResolution.uniquenessId);
		resolutionPool.undo(ElectionMasterResolution.uniquenessId);
	}
	
	
	/**
	 * 关闭
	 */
	public void close(final ICloseCallback callback) {
		//	关闭通讯器
		com.close(new IServer.ICloseCallback() {
			public void onSucc() {
				if (callback != null) {callback.onSucc();}
			}
			public void onError(Throwable t) {
				if (callback != null) {callback.onError(t);}
			}
		});
	}
	/**
	 * 关闭回调
	 */
	public static interface ICloseCallback {
		void onSucc();
		void onError(Throwable t);
	}
	
	
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public ICom getCom() {
		return com;
	}
	public void setCom(ICom com) {
		this.com = com;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public RuleEnums getRule() {
		return rule;
	}
	public void setRule(RuleEnums rule) {
		this.rule = rule;
	}
	public MasterInfo getLeaderInfo() {
		return leaderInfo;
	}
	public void setLeaderInfo(MasterInfo leaderInfo) {
		this.leaderInfo = leaderInfo;
	}
	public CommunicationList getCommunicationList() {
		return communicationList;
	}
	public void setCommunicationList(CommunicationList communicationList) {
		this.communicationList = communicationList;
	}
	public ResolutionPool getResolutionPool() {
		return resolutionPool;
	}
	public void setResolutionPool(ResolutionPool resolutionPool) {
		this.resolutionPool = resolutionPool;
	}
	public ConfirmResourceCallbackPool getConfirmPool() {
		return confirmPool;
	}
	public void setConfirmPool(ConfirmResourceCallbackPool confirmPool) {
		this.confirmPool = confirmPool;
	}
	
	
	
	@Override
	public String toString() {
		return new StringBuilder()
					.append("[")
					.append("rule:").append(getRule()).append("-")
					.append("info:").append(self()).append("-")
					.append("]")
					.toString();
	}
	
	
}
