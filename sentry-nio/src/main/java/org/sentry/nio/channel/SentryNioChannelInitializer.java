package org.sentry.nio.channel;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.sentry.nio.commons.SentryNioConfiguration;
import org.sentry.nio.handly.IHandlerAppender;
import org.sentry.nio.handly.idle.IdleEventHandler;
import org.sentry.nio.msg.IMsgCoderAppender;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


/**
 * 
 * 
 * @author luoyi
 *
 */
public class SentryNioChannelInitializer extends ChannelInitializer<SocketChannel> {
	
	
	static Logger log = Logger.getLogger("server-session-close");
	
	
	protected ChannelGroup channelGroup;
	
	//	消息编解码器Appender
	protected IMsgCoderAppender msgCoderAppender;
	//	消息处理类Appender
	protected IHandlerAppender handlerAppender;
	
	
	//	idle事件处理（默认逻辑是直接关掉）
	protected IdleEventHandler idleHandler = new IdleEventHandler();
	
	//	channel相关状态变更回调
	protected IChannelCallback channelCallback;
	
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
	}
	

	/**
	 * 初始化的连接每个channel必须独立对象
	 */
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if (channelCallback != null) {
			if (!channelCallback.beforeChannelInit(ch, channelGroup)) {
				ch.close();

				return;
			}
		}
		
		//	追加日志handler
//		ch.pipeline().addLast("logging-handler", new LoggingHandler(LogLevel.DEBUG));
		
		//	追加idle监控handler
		if (SentryNioConfiguration.isIdle()) {
			ch.pipeline().addLast(
					"idle-handler", 
					new IdleStateHandler(
							SentryNioConfiguration.getIdleTimeoutRead(), 
							SentryNioConfiguration.getIdleTimeoutWrite(), 
							SentryNioConfiguration.getIdleTimeoutAll(), 
							TimeUnit.SECONDS));
			
			//	追加idle触发时的handler
			idleHandler.setChannelCallback(channelCallback);
			ch.pipeline().addLast("idle-event-handler", new IdleEventHandler());
		}
		
		//	追加编码解码
		msgCoderAppender.appendCoder(ch.pipeline());
		
		//	追加in,out handler
		handlerAppender.appendHandler(ch.pipeline());
		
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("channel exception. id:" + ctx.channel().id(), cause);
		cause.printStackTrace();
		
		if (channelCallback != null) {
			if (!channelCallback.onChannelException(cause, ctx, channelGroup)) {
				ctx.channel().close();
			}
		}
		
		super.exceptionCaught(ctx, cause);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		
		channelGroup.add(ctx.channel());
	}

	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		
		log.error("channel closed. id:" + ctx.channel().id());
		System.out.println("channel closed. id:" + ctx.channel().id());
		
		channelGroup.remove(ctx.channel());
	}

	public ChannelGroup getChannelGroup() {
		return channelGroup;
	}

	public void setChannelGroup(ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
	}

	public IMsgCoderAppender getMsgCoderAppender() {
		return msgCoderAppender;
	}

	public void setMsgCoderAppender(IMsgCoderAppender msgCoderAppender) {
		this.msgCoderAppender = msgCoderAppender;
	}

	public IdleEventHandler getIdleHandler() {
		return idleHandler;
	}

	public void setIdleHandler(IdleEventHandler idleHandler) {
		this.idleHandler = idleHandler;
	}

	public IHandlerAppender getHandlerAppender() {
		return handlerAppender;
	}

	public void setHandlerAppender(IHandlerAppender handlerAppender) {
		this.handlerAppender = handlerAppender;
	}

	public IChannelCallback getChannelCallback() {
		return channelCallback;
	}

	public void setChannelCallback(IChannelCallback channelCallback) {
		this.channelCallback = channelCallback;
	}
	
}
