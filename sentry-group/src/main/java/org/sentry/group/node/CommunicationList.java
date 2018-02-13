package org.sentry.group.node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.sentry.group.App;
import org.sentry.group.commons.GroupConfiguration;

/**
 * 其他节点清单
 * 
 * @author luoyi
 *
 */
public class CommunicationList {

	
	static Logger log = Logger.getLogger("sentry-group");
	
	
	//	通讯清单（从配置文件中读取，过滤掉自己）
	protected Set<Info> infoList = new HashSet<Info>();
	
	
	public void init() {
		log.info("[CommunicationList] init communication list...");
		
		String hosts = GroupConfiguration.getHosts();
		String[] ss = hosts.split(";");
		for (String s : ss) {
			String[] sss = s.split(":");
			if (sss.length < 2) {
				log.warn("[CommunicationList] init warn. can't parse info:" + s);
				continue;
			}
			
			try {
				String host = sss[0];
				Integer port = Integer.parseInt(sss[1]);
				Info info = new Info(host, port);
				//	如果host和port都是自己则不予连接
				if (App.ME.isSelf(info)) {
					continue ;
				}
				infoList.add(info);
				log.info("[CommunicationList] init info:" + s);
			}catch (Exception e) {
				log.error("[CommunicationList] init warn. can't parse info:" + s);
				continue;
			}
		}
	}
	/**
	 * 追加info
	 */
	public void append(String host, int port) {
		infoList.add(new Info(host, port));
		
		log.info("[CommunicationList] append info:" + (host + ":" + port));
	}
	/**
	 * 取当前infos
	 */
	public Set<Info> allInfos() {return infoList;}
	/**
	 * 是否包含info
	 */
	public boolean exists(CommunicationList.Info info) {
		return infoList.contains(info);
	}
	/**
	 * 追加info
	 */
	public void append(CommunicationList.Info info) {
		infoList.add(info);
	}
	
	
	
	
	/**
	 * 节点信息
	 */
	public static class Info implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		//	主机信息
		protected String host;
		protected int port;
		public Info() {
			super();
		}
		public Info(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}
		public String getHost() {
			return host;
		}
		public void setHost(String host) {
			this.host = host;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
		@Override
		public int hashCode() {
			return (host + ":" + port).hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Info)) {return false;}
			
			Info info = (Info)obj;
			return (host + ":" + port).equals(info.host + ":" + info.port);
		}
		@Override
		public String toString() {
			return host + ":" + port;
		}
	}
}
