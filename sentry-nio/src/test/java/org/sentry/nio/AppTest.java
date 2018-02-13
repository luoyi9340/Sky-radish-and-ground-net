package org.sentry.nio;

import org.junit.Before;
import org.junit.Test;
import org.sentry.nio.client.IClient;
import org.sentry.nio.client.IClient.IConnectCallback;
import org.sentry.nio.server.IServer;
import org.sentry.nio.server.IServer.IStartCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.netty.channel.Channel;


/**
 * 
 * @author luoyi
 *
 */
public class AppTest {

	
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
		testServer.start(new IStartCallback() {
			public void onSucc() {
				testClient();
			}
			public void onError(Throwable t) {
			}
		});
		
		
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	protected void testClient() {
		testClient.connect(new IConnectCallback() {
			public void onConnectSucc(Channel channel) {
				
			}
			public void onConnectError(Throwable t) {
			}});
	}
	
	
}
