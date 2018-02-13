package org.netty.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.TypeParameterMatcher;


/**
 * 分类型写消息后置处理器
 * 
 * <p>	参考SimpleChannelInboundHandler
 * 
 * @author luoyi
 *
 * @param <T>
 */
public abstract class SimpleChannelOutboundHandler<T> extends ChannelOutboundHandlerAdapter {

	
	private final TypeParameterMatcher matcher;
	
	
	public SimpleChannelOutboundHandler() {
		matcher = TypeParameterMatcher.find(this, SimpleChannelOutboundHandler.class, "T");
	}

	
    protected SimpleChannelOutboundHandler(Class<? extends T> inboundMessageType) {
    	matcher = TypeParameterMatcher.get(inboundMessageType);
    }


    public boolean acceptInboundMessage(Object msg) throws Exception {
        return matcher.match(msg);
    }

    
	@Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        try {
            if (acceptInboundMessage(msg)) {
                @SuppressWarnings("unchecked")
                T imsg = (T) msg;
                channelWrite0(ctx, imsg);
            } else {
                ctx.fireChannelRead(msg);
            }
        } finally {
        	super.write(ctx, msg, promise);
        }
    }

	
	/**
	 * 消息写后置方法
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	protected abstract void channelWrite0(ChannelHandlerContext ctx, T msg) throws Exception;
	
}
