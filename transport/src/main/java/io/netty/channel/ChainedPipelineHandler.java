package io.netty.channel;

import io.netty.buffer.Buf;

import java.net.SocketAddress;


public final class ChainedPipelineHandler implements ChannelInboundHandler, ChannelOutboundHandler {
    private EmbeddedChannelPipeline pipeline;

    @Override
    public Buf newInboundBuffer(ChannelHandlerContext ctx) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void freeInboundBuffer(ChannelHandlerContext ctx) throws Exception {
        // do nothing
    }

    @Override
    public Buf newOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void freeOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
        // do nothing
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        pipeline.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        pipeline.connect(remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        pipeline.disconnect(promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        pipeline.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        pipeline.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) {
        pipeline.read();
    }

    @Override
    public void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        pipeline.flush(promise);
    }

    @Override
    public void sendFile(ChannelHandlerContext ctx, FileRegion region, ChannelPromise promise) throws Exception {
        pipeline.sendFile(region, promise);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        pipeline.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        pipeline.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        pipeline.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        pipeline.fireChannelInactive();
    }

    @Override
    public void channelReadSuspended(ChannelHandlerContext ctx) throws Exception {
        // NOOP
    }

    @Override
    public void inboundBufferUpdated(ChannelHandlerContext ctx) throws Exception {
        pipeline.fireInboundBufferUpdated();
    }

    @Override
    public void beforeAdd(ChannelHandlerContext ctx) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterAdd(ChannelHandlerContext ctx) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void beforeRemove(ChannelHandlerContext ctx) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void afterRemove(ChannelHandlerContext ctx) throws Exception {
        for (ChannelHandler handler: pipeline.toMap().values()) {
            pipeline.remove(handler);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        pipeline.fireExceptionCaught(cause);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        pipeline.fireUserEventTriggered(evt);
    }

    private final class EmbeddedChannelPipeline extends AbstractChannelPipeline {
        private EmbeddedChannelPipeline(Channel channel) {
            super(channel);
        }

        @Override
        protected ChannelOutboundHandler newHeadHandler() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        protected ChannelInboundHandler newTailHandler() {
            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
