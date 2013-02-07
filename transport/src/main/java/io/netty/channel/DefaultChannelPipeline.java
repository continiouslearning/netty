/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Freeable;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;

import java.net.SocketAddress;


final class DefaultChannelPipeline extends AbstractChannelPipeline {

    private final static TailHandler TAIL_HANDLER = new TailHandler();
    private final static ByteHeadHandler BYTE_HEAD_HANDLER = new ByteHeadHandler();
    private final static MessageHeadHandler MESSAGE_HEAD_HANDLER = new MessageHeadHandler();

    public DefaultChannelPipeline(Channel channel) {
        super(channel);
    }

    @Override
    protected ChannelOutboundHandler newHeadHandler() {
        switch (channel.metadata().bufferType()) {
            case BYTE:
                return BYTE_HEAD_HANDLER;
            case MESSAGE:
                return MESSAGE_HEAD_HANDLER;
            default:
                throw new Error("unknown buffer type: " + channel.metadata().bufferType());
        }
    }

    @Override
    protected ChannelInboundHandler newTailHandler() {
        return TAIL_HANDLER;
    }

    private static final class TailHandler extends ChannelInboundMessageHandlerAdapter<Freeable> {
        public TailHandler() {
            super(Freeable.class);
        }

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, Freeable msg) throws Exception {
            if (logger.isWarnEnabled()) {
                logger.warn("Freeable reached end-of-pipeline, call " + msg + ".free() to" +
                        " guard against resource leakage!");
            }
            msg.free();
        }
    }

    private abstract static class HeadHandler implements ChannelOutboundHandler {
        @Override
        public final void beforeAdd(ChannelHandlerContext ctx) throws Exception {
            // NOOP
        }

        @Override
        public final void afterAdd(ChannelHandlerContext ctx) throws Exception {
            // NOOP
        }

        @Override
        public final void beforeRemove(ChannelHandlerContext ctx) throws Exception {
            // NOOP
        }

        @Override
        public final void afterRemove(ChannelHandlerContext ctx) throws Exception {
            // NOOP
        }

        @Override
        public final void bind(
                ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise)
                throws Exception {
            ctx.channel().unsafe().bind(localAddress, promise);
        }

        @Override
        public final void connect(
                ChannelHandlerContext ctx,
                SocketAddress remoteAddress, SocketAddress localAddress,
                ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().connect(remoteAddress, localAddress, promise);
        }

        @Override
        public final void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().disconnect(promise);
        }

        @Override
        public final void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().close(promise);
        }

        @Override
        public final void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().deregister(promise);
        }

        @Override
        public final void read(ChannelHandlerContext ctx) {
            ctx.channel().unsafe().beginRead();
        }

        @Override
        public final void flush(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().flush(promise);
        }

        @Override
        public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.fireExceptionCaught(cause);
        }

        @Override
        public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public final void sendFile(
                ChannelHandlerContext ctx, FileRegion region, ChannelPromise promise) throws Exception {
            ctx.channel().unsafe().sendFile(region, promise);
        }
    }

    private static final class ByteHeadHandler extends HeadHandler implements ChannelOutboundByteHandler {
        @Override
        public ByteBuf newOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
            return ctx.alloc().ioBuffer();
        }

        @Override
        public void discardOutboundReadBytes(ChannelHandlerContext ctx) throws Exception {
            if (ctx.hasOutboundByteBuffer()) {
                ctx.outboundByteBuffer().discardSomeReadBytes();
            }
        }

        @Override
        public void freeOutboundBuffer(ChannelHandlerContext ctx) {
            ctx.outboundByteBuffer().free();
        }
    }

    private static final class MessageHeadHandler extends HeadHandler implements ChannelOutboundMessageHandler<Object> {
        @Override
        public MessageBuf<Object> newOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
            return Unpooled.messageBuffer();
        }

        @Override
        public void freeOutboundBuffer(ChannelHandlerContext ctx) {
            ctx.outboundMessageBuffer().free();
        }
    }
}
