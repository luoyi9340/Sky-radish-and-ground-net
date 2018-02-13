package org.sentry.nio.commons;

import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;


/**
 * 哨兵模式的nio channel_group
 * 
 * @author luoyi
 *
 */
public class SentryNioChannelGroup extends DefaultChannelGroup {

	
	public SentryNioChannelGroup(EventExecutor executor) {
		super(executor);
	}
	
	
	public SentryNioChannelGroup(String name, EventExecutor executor) {
		super(name, executor);
	}
	
	
	public SentryNioChannelGroup(String name, EventExecutor executor, boolean stayClosed) {
		super(name, executor, stayClosed);
	}


}
