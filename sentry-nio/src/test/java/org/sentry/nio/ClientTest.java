package org.sentry.nio;

import org.junit.Before;
import org.junit.Test;
import org.sentry.nio.client.IClient;
import org.sentry.nio.server.IServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author luoyi
 *
 */
public class ClientTest {
	
	
	IServer testServer;
	IClient testClient;
	
	
	@Before
	public void init() {
		ApplicationContext context = new ClassPathXmlApplicationContext("conf/spring/spring-test.xml");
		
		testServer = context.getBean("testServer", IServer.class);
		testClient = context.getBean("testClient", IClient.class);
	}
	
	
	@Test
	public void test() {
		testClient.connect(null);
		
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
