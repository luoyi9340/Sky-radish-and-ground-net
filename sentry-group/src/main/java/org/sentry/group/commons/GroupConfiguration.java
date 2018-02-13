package org.sentry.group.commons;

import org.sentry.commons.Configuration;


/**
 * 
 * @author luoyi
 *
 */
public class GroupConfiguration extends Configuration {

	
	/**
	 * 取port
	 */
	public static int getPort() {
		return get("sentry-group.port", 23001);
	}
	
	/**
	 * 取hosts
	 */
	public static String getHosts() {
		return get("sentry-group.hosts", "");
	}
	
	
	/**
	 * 通讯socket相关
	 */
	public static long getSocketTimeout() {
		return get("sentry-group.socket.timeout", 3000l);
	}
	
	
	/**
	 * 节点之间互相ping间隔
	 */
	public static long getPingInterval() {
		return get("sentry-group.socket.ping.interval", 5000l);
	}
	
	
	/**
	 * 决议相关
	 */
	public static long getResolutionTimeout() {
		return get("sentry-group.resolution.timeout", 3000l);
	}
	
	
	/**
	 * 寻主决议报名截止时间
	 */
	public static long getSentryGroupResolutionAskMasterSignupInterval() {
		return get("sentry-group.resolution.ask_master.signup.interval", 3000);
	}
	/**
	 * 寻主决议投票截止时间
	 */
	public static long getSentryGroupResolutionAskMasterVoteInterval() {
		return get("sentry-group.resolution.ask_master.vote.interval", 3000);
	}
	
	
	/**
	 * 确认主是否可用决议报名间隔
	 */
	public static long getSentryGroupResolutionConfirmMasterSignupInterval() {
		return get("sentry-group.resolution.confirm_master.signup.interval", 3000);
	}
	/**
	 * 确认主是否可用决议间隔
	 */
	public static long getSentryGroupResolutionConfirmMasterVoteInterval() {
		return get("sentry-group.resolution.confirm_master.vote.interval", 3000);
	}
	
	
	/**
	 * 选主报名间隔
	 */
	public static long getSentryGroupResolutionElectionMasterSignupInterval() {
		return get("sentry-group.resolution.election_master.signup.interval", 3000);
	}
	/**
	 * 选主决议间隔
	 */
	public static long getSentryGroupResolutionElectionMasterVoteInterval() {
		return get("sentry-group.resolution.election_master.vote.interval", 3000);
	}
}
