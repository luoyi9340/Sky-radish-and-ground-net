package org.sentry.commons;

import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.InitializingBean;


/**
 * Log初始化
 * 
 * @author luoyi
 *
 */

public class LogIniter implements InitializingBean{

	
	//	log4j.xml
	protected String log4j = "conf/logs/log4j.xml";
	
	
	public void afterPropertiesSet() throws Exception {
		DOMConfigurator.configure(Thread.currentThread().getContextClassLoader().getResource(log4j));
		LogFactory.getLog(LogIniter.class).info("log4j 初始化成功.");
	}


	public String getLog4j() {
		return log4j;
	}


	public void setLog4j(String log4j) {
		this.log4j = log4j;
	}
	
}
