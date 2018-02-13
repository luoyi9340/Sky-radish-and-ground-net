package org.sentry.nio.commons;

import org.sentry.commons.Configuration;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelOption;


/**
 * 相关配置信息
 * 
 * @author luoyi
 *
 */
public class SentryNioConfiguration extends Configuration {


	/**
	 * group线程数
	 */
	public static int getGroupThreadPoolSize() {
		return get("sentry-nio.netty.group", 4);
	}
	/**
	 * boss线程数
	 */
	public static int getBossThreadPoolSize() {
		return get("sentry-nio.netty.boss", 4);
	}
	
	
	
	
	/***************************************************************************************************
	 * IDLE各种配置
	 ***************************************************************************************************/
	/**
	 * 取idle是否开启
	 */
	public static boolean isIdle() {
		return get("sentry-nio.netty.idle", true);
	}
	/**
	 * 取idle read超时时间
	 */
	public static int getIdleTimeoutRead() {
		return get("sentry-nio.netty.idle.timeout.read", 60);
	}
	/**
	 * 取idle write超时时间
	 */
	public static int getIdleTimeoutWrite() {
		return get("sentry-nio.netty.idle.timeout.write", 60);
	}
	/**
	 * 取idle all超时时间
	 */
	public static int getIdleTimeoutAll() {
		return get("sentry-nio.netty.idle.timeout.all", 60);
	}
	
	
	
	
	/***************************************************************************************************
	 * ChannelOption各种配置
	 ***************************************************************************************************/
	/**
	 * 取SO_BACKLOG
	 */
	public static int getOptionSO_BACKLOG() {
		return get("sentry-nio.netty.option.SO_BACKLOG", 1024);
	}
	/**
	 * 取SO_REUSEADDR
	 */
	public static boolean isOptionSO_REUSEADDR() {
		return get("sentry-nio.netty.option.SO_REUSEADDR", false);
	}
	/**
	 * 取SO_KEEPALIVE
	 */
	public static boolean isOptionSO_KEEPALIVE() {
		return get("sentry-nio.netty.opton.SO_KEEPALIVE", true);
	}
	/**
	 * 取SO_SNDBUF
	 */
	public static int getOptionSO_SNDBUF() {
		return get("sentry-nio.netty.option.SO_SNDBUF", 4096);
	}
	/**
	 * 取SO_RCVBUF
	 */
	public static int getOptionSO_RCVBUF() {
		return get("sentry-nio.netty.option.SO_RCVBUF", 4096);
	}
	/**
	 * 取TCP_NODELAY
	 */
	public static boolean isOptionTCP_NODELAY() {
		return get("sentry-nio.netty.option.TCP_NODELAY", true);
	}
	/**
	 * 取SO_LINGER
	 */
	public static int isOptionSO_LINGER() {
		return get("sentry-nio.netty.option.SO_LINGER", 1000);
	}
	
	
	
	
	/***************************************************************************************************
	 * ByteBuf内存各种配置
	 ***************************************************************************************************/
	/**
	 * 是否堆外内存
	 */
	public static boolean isMemberDirect() {
		return get("sentry-nio.netty.option.member.direct", true);
	}
	/**
	 * 堆外内存最大可分配（单位：B，默认不限）
	 */
	public static long getMemberMaxDirect() {
		return get("sentry-nio.netty.option.member.max_direct", -1l);
	}
	/**
	 * 是否池化内存
	 */
	public static boolean isMemberPool() {
		return get("sentry-nio.netty.option.member.pool", true);
	}
	


	/**
	 * 设置配置项
	 */
	public static void config(ServerBootstrap strap) {
		//	追加各种ChannelOption配置
		strap.option(ChannelOption.SO_BACKLOG, SentryNioConfiguration.getOptionSO_BACKLOG());
		strap.option(ChannelOption.SO_REUSEADDR, SentryNioConfiguration.isOptionSO_REUSEADDR());
		strap.option(ChannelOption.SO_KEEPALIVE, SentryNioConfiguration.isOptionSO_KEEPALIVE());
		strap.option(ChannelOption.SO_SNDBUF, SentryNioConfiguration.getOptionSO_SNDBUF());
		strap.option(ChannelOption.SO_RCVBUF, SentryNioConfiguration.getOptionSO_RCVBUF());
		strap.option(ChannelOption.TCP_NODELAY, SentryNioConfiguration.isOptionTCP_NODELAY());
		strap.option(ChannelOption.SO_LINGER, SentryNioConfiguration.isOptionSO_LINGER());
		
		
		//	追加内存配置
		if (SentryNioConfiguration.isMemberPool()) {
			strap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		}else {
			strap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
		}
		//	是否堆外内存
		if (SentryNioConfiguration.isMemberDirect()) {
//			System.setProperty("io.netty.noUnsafe", Boolean.TRUE.toString());
			
			//	启用堆外时设置最大堆外能用到多少
			if (SentryNioConfiguration.getMemberMaxDirect() > 0) {
				System.setProperty("io.netty.maxDirectMemory", SentryNioConfiguration.getMemberMaxDirect() + "");
			}
		}else {
			System.setProperty("io.netty.noUnsafe", Boolean.FALSE.toString());
		}
	}
	/**
	 * 设置配置项
	 */
	public static void config(Bootstrap strap) {
		//	追加各种ChannelOption配置
		strap.option(ChannelOption.SO_BACKLOG, SentryNioConfiguration.getOptionSO_BACKLOG());
		strap.option(ChannelOption.SO_REUSEADDR, SentryNioConfiguration.isOptionSO_REUSEADDR());
		strap.option(ChannelOption.SO_KEEPALIVE, SentryNioConfiguration.isOptionSO_KEEPALIVE());
		strap.option(ChannelOption.SO_SNDBUF, SentryNioConfiguration.getOptionSO_SNDBUF());
		strap.option(ChannelOption.SO_RCVBUF, SentryNioConfiguration.getOptionSO_RCVBUF());
		strap.option(ChannelOption.TCP_NODELAY, SentryNioConfiguration.isOptionTCP_NODELAY());
		strap.option(ChannelOption.SO_LINGER, SentryNioConfiguration.isOptionSO_LINGER());
		
		
		//	追加内存配置
		if (SentryNioConfiguration.isMemberPool()) {
			strap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
		}else {
			strap.option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT);
		}
		//	是否堆外内存
		if (SentryNioConfiguration.isMemberDirect()) {
//			System.setProperty("io.netty.noUnsafe", Boolean.TRUE.toString());
			
			//	启用堆外时设置最大堆外能用到多少
			if (SentryNioConfiguration.getMemberMaxDirect() > 0) {
				System.setProperty("io.netty.maxDirectMemory", SentryNioConfiguration.getMemberMaxDirect() + "");
			}
		}else {
			System.setProperty("io.netty.noUnsafe", Boolean.FALSE.toString());
		}
	}

}
