package com.zy.netty.netty02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class Server02 {

    private static final String DELIMITER_SEPARATOR = "$_";

    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("server-bossGroup", true));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("server-workerGroup", true));

        try {
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 这里结合 DelimiterBasedFrameDecoder + StringDecoder 实现粘包拆包的处理, 其中 DelimiterBasedFrameDecoder 需要自定义分隔符, 否则走默认值
                                    // 这里需要注意: 单条消息不能超过给定的最大限度, 否则会抛出异常
                                    .addLast("$_delimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(4096, Unpooled.copiedBuffer(DELIMITER_SEPARATOR.getBytes(StandardCharsets.UTF_8))))
                                    .addLast("stringDecoder", new StringDecoder())
                                    .addLast("stringEncoder", new StringEncoder())
                                    .addLast(new ServerHandler02());
                        }
                    });

            ChannelFuture channelFuture = server.bind("127.0.0.1", 8090).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server is error ..........", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class ServerHandler02 extends SimpleChannelInboundHandler<Object> {
        // 保存所有与 服务端 建立好连接的 客户端的 channel 对象
        // FIXME 分布式场景下, 是否可以存储到 redis 中 ?
        private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel channel = ctx.channel();
            channels.forEach(ch -> {
                if (Objects.equals(ch, channel)) {
                    ch.writeAndFlush("self send msg: " + msg + DELIMITER_SEPARATOR);
                } else {
                    ch.writeAndFlush("remoteAddress: " + channel.remoteAddress() + " send msg: " + msg + DELIMITER_SEPARATOR);
                }
            });
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            // 有客户端加入的事件
            Channel channel = ctx.channel();
            // 广播:
            channels.add(channel);
            channels.writeAndFlush("remoteAddress: " + channel.remoteAddress() + " join the server" + DELIMITER_SEPARATOR);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            // 有客户端离开的事件
            Channel channel = ctx.channel();
            // 广播:
            channels.writeAndFlush("remoteAddress: " + channel.remoteAddress() + " leave the server" + DELIMITER_SEPARATOR);
            channels.remove(channel);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 客户端处于活动状态
            Channel channel = ctx.channel();
            System.out.println("remoteAddress: " + channel.remoteAddress() + " is online.");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            // 客户端处于非活动状态
            Channel channel = ctx.channel();
            System.out.println("remoteAddress: " + channel.remoteAddress() + " is offline.");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
