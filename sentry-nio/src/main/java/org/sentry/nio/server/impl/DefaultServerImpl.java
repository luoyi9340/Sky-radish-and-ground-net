package org.sentry.nio.server.impl;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sentry.nio.channel.SentryNioChannelInitializer;
import org.sentry.nio.channel.SentryNioServerSocketChannel;
import org.sentry.nio.commons.SentryNioChannelGroup;
import org.sentry.nio.commons.SentryNioConfiguration;
import org.sentry.nio.msg.IMsg;
import org.sentry.nio.server.IServer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;


/**
 * netty实现的nio服务端
 * 
 * @author luoyi
 *
 */
public class DefaultServerImpl implements IServer, InitializingBean {
	
	
	static Logger log = Logger.getLogger("sentry-nio");

	
	//	服务端口号
	protected int port = 21001;
	//	服务命名（默认[server-${端口号}]）
	protected String serverName;
	
	
	//	channelGroup
	protected ChannelGroup channelGroup;
	//	线程执行器
	protected ServerBootstrap strap;
	//	accept线程组
	protected EventLoopGroup bossGroup;
	protected int bossGroupSize = 2;
	//	io线程组
	protected EventLoopGroup workerGroup;
	protected int workerGroupSize = 10;
	//	是否已启动
	protected volatile boolean running;

	
	//	子channel
	protected SentryNioChannelInitializer channelInitializer;
	
	
	public void afterPropertiesSet() throws Exception {
		init();
	}
	public void init() {
		//	服务命名
		if (StringUtils.isEmpty(serverName)) {serverName = "[server-" + port + "]";}
		
		//	accept线程组
		bossGroup = new NioEventLoopGroup(bossGroupSize);
		//	worker线程组（推荐cpu核数 * 2）
		workerGroup = new NioEventLoopGroup(workerGroupSize);
		
		strap = new ServerBootstrap();
		strap.group(bossGroup, workerGroup);
		strap.channel(SentryNioServerSocketChannel.class);
		
		//	追加配置
		SentryNioConfiguration.config(strap);
		
		strap.childHandler(channelInitializer);
		
		if (channelGroup == null) {
			channelGroup = new SentryNioChannelGroup("sentry-channel-group", new DefaultEventExecutor());
		}
		
		channelInitializer.setChannelGroup(channelGroup);
	}


	//	主channel
	protected Channel serverChannel;
	
	public void start(final IServer.IStartCallback callback) {
		if (running) {return;}
		
		strap.bind(port)
				.addListener(new GenericFutureListener<ChannelFuture>() {
								public void operationComplete(ChannelFuture future) throws Exception {
									//	start success.
									if (future.isSuccess()) {
										running = true;
										
										log.info(serverName + " server has started.");
										serverChannel = future.channel();
										
										if (callback != null) {
											callback.onSucc();
										}
									}else {
										log.info(serverName + " server start error " + future.cause().getMessage(), future.cause());
										
										if (callback != null) {
											callback.onError(future.cause());
										}
									}
								}
							});
	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void close(final IServer.ICloseCallback callback) {
		try {
			channelGroup.close().awaitUninterruptibly();
			
			//	关闭主channel
			if (serverChannel != null) {
				serverChannel.disconnect().awaitUninterruptibly();
				serverChannel.close().awaitUninterruptibly();
			}
			
			//	关闭线程组
			//	关闭accept，不再接收新的连接
			bossGroup.shutdownGracefully(2, 15, TimeUnit.SECONDS).addListener(new GenericFutureListener() {
				@Override
				public void operationComplete(Future future) throws Exception {
					if (future.isSuccess()) {
						log.info("[DefaultServerImpl] bossGroup close success. ");
						
						//	关闭io
						workerGroup.shutdownGracefully(2, 15, TimeUnit.SECONDS).addListener(new GenericFutureListener() {
							public void operationComplete(Future future) throws Exception {
								if (future.isSuccess()) {
									log.info("[DefaultServerImpl] workerGroup close success. ");
									
									running = false;
									
									//	所有的回调都执行成功后才判定服务已关闭
									log.info(serverName + " server has closed.");
									
									if (callback != null) {
										//	TODO	等待资源释放，实在不知道哪里会占用端口资源。这里做个等待
										Thread.sleep(5000);
										
										callback.onSucc();
									}
								}else {
									log.info("[DefaultServerImpl] workerGroup close unsucc. cause:" + future.cause());
								}
							}
						});
					}else {
						log.info("[DefaultServerImpl] bossGroup close unsucc. cause:" + future.cause());
					}
				}
			});
		}catch (Throwable t) {
			log.error(serverName + " server closed error." + t.getMessage(), t);
			
			if (callback != null) {
				callback.onError(t);
			}
		}
	}

	
	@Override
	public void write(Channel channel, IMsg msg) {
		channel.write(msg);
	}
	@Override
	public void writeAndFlush(Channel channel, IMsg msg) {
		channel.writeAndFlush(msg);
	}


	public boolean isRunning() {
		return running;
	}

	public ChannelGroup channelGroup() {
		return this.channelGroup;
	}

	public SentryNioChannelInitializer getChannelInitializer() {
		return channelInitializer;
	}

	public void setChannelInitializer(SentryNioChannelInitializer channelInitializer) {
		this.channelInitializer = channelInitializer;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBossGroupSize() {
		return bossGroupSize;
	}

	public void setBossGroupSize(int bossGroupSize) {
		this.bossGroupSize = bossGroupSize;
	}

	public int getWorkerGroupSize() {
		return workerGroupSize;
	}

	public void setWorkerGroupSize(int workerGroupSize) {
		this.workerGroupSize = workerGroupSize;
	}

}
