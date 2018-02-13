package org.sentry.nio.client.impl;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sentry.nio.channel.SentryNioChannelInitializer;
import org.sentry.nio.channel.SentryNioClientSocketChannel;
import org.sentry.nio.client.IClient;
import org.sentry.nio.commons.SentryNioChannelGroup;
import org.sentry.nio.commons.SentryNioConfiguration;
import org.sentry.nio.msg.IMsg;
import org.springframework.beans.factory.InitializingBean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;


/**
 * netty实现的nio客户端
 * 
 * @author luoyi
 *
 */
public class DefaultClientImpl implements IClient, InitializingBean {
	
	
	static Logger log = Logger.getLogger("sentry-nio");
	
	
	//	默认的io线程组线程数
	public static final int DEFAULT_IO_THREAD_POOL_SIZE = 10;
	
	
	//	host，port
	protected String host;
	protected int port;
	
	//	线程池大小
	protected int threadPoolSize = 1;
	
	protected ChannelGroup channelGroup;
	
	//	线程组
	protected EventLoopGroup workerGroup;
	//	线程执行器
	protected Bootstrap strap;
	//	连接通道
	protected Channel channel;
	//	channel初始化
	protected SentryNioChannelInitializer channelInitializer;
	//	是否在跑
	protected boolean running;
	
	
	//	断连回调
	protected IDisconnectCallback disconnectCallback;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	/**
	 * 初始化
	 */
	public void init() {
		//	一些初始化的配置
		workerGroup = new NioEventLoopGroup(threadPoolSize);
		strap = new Bootstrap();
		strap.group(workerGroup)
			.channel(SentryNioClientSocketChannel.class)
			.handler(channelInitializer);
		
		if (channelGroup == null) {
			channelGroup = new SentryNioChannelGroup("sentry-channel-group", new DefaultEventExecutor());
		}
		channelInitializer.setChannelGroup(channelGroup);
		
		//	加载各种配置
		SentryNioConfiguration.config(strap);
	}

	
	public void connect(IConnectCallback callback) {
		connect(this.host, this.port, callback);
	}
	@Override
	public void connect(String host, int port, final IConnectCallback callback) {
		this.host = host;
		this.port = port;
		strap.connect(host, port).addListener(new GenericFutureListener<ChannelFuture>() {
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					log.info("client connect succ. host:" + DefaultClientImpl.this.host + " port:" + DefaultClientImpl.this.port);
					
					running = true;
					channel = future.channel();
					
					if (callback != null) {
						callback.onConnectSucc(channel);
					}
				}else {
					log.error("client connect error. host:" + DefaultClientImpl.this.host + " port:" + DefaultClientImpl.this.port + " " + future.cause().getMessage(), future.cause());
					future.cause().printStackTrace();
					if (callback != null) {
						callback.onConnectError(future.cause());
					}
				}
			}
		});
	}
	
	
	public void disconnect(final ICloseCallback callback) {
		try {
			this.running = false;
			if (channel != null && channel.isActive()) {
				channel.close();
			}
			workerGroup.shutdownGracefully(2, 15, TimeUnit.SECONDS);
			System.out.println("客户端关闭成功.");
			
			if (callback != null) {
				callback.onCloseSucc();
			}
		}catch (Throwable t) {
			if (callback !=  null) {
				callback.onCloseError(t);
			}
		}
	}
	
	
	@Override
	public void addDisconnectCallback(IDisconnectCallback callback) {
		this.disconnectCallback = callback;
	}
	
	
	public void write(IMsg msg) {
		channel.write(msg).addListener(new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.cause() != null) {
					//	如果连接已经断开则执行断连回调（已发消息时发现的断连为准）
					if (future.getClass().equals(ClosedChannelException.class)) {
						log.warn("[IClient] has disconnect. host:" + host + " port:" + port);
						
						if (disconnectCallback != null) {
							disconnectCallback.onDisconnect(host, port);
						}
					}
				}
			}
		});
	}
	public void writeAndFlush(IMsg msg) {
		channel.writeAndFlush(msg).addListener(new GenericFutureListener<Future<? super Void>>() {
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.cause() != null) {
					//	如果连接已经断开则执行断连回调（已发消息时发现的断连为准）
					if (future.getClass().equals(ClosedChannelException.class)) {
						log.warn("[IClient] has disconnect. host:" + host + " port:" + port);
						
						if (disconnectCallback != null) {
							disconnectCallback.onDisconnect(host, port);
						}
					}
				}
			}
		});
	}

	public boolean isConnect() {
		return running;
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

	public SentryNioChannelInitializer getChannelInitializer() {
		return channelInitializer;
	}

	public void setChannelInitializer(SentryNioChannelInitializer channelInitializer) {
		this.channelInitializer = channelInitializer;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}

	public ChannelGroup getChannelGroup() {
		return channelGroup;
	}

	public void setChannelGroup(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}

}
