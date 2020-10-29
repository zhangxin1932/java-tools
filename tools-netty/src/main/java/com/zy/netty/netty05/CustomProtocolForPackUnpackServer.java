package com.zy.netty.netty05;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义协议解决 tcp 传输的粘包 拆包问题
 * 服务端
 */
public class CustomProtocolForPackUnpackServer {

    private static final int PROCESSORS = 2 * Runtime.getRuntime().availableProcessors() + 1;
    // 生产中, 为避免业务线程阻塞, 这里定义 业务线程池
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(PROCESSORS);

    public static void main(String[] args) {
        EventLoopGroup parentGroup = new NioEventLoopGroup(1);
        EventLoopGroup childGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(parentGroup, childGroup).channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // .option() // parentGroup 通过ChannelConfig, 配置与Channel相关的, 网络层相关的配置
                    // .childOption() // childGroup 通过ChannelConfig, 配置与Channel相关的, 网络层相关的配置
                    // .attr() // parentGroup 主要维护业务运行数据, 在运行时候, 向程序中动态添加业务数据
                    // .childAttr() // childGroup 主要维护业务运行数据, 在运行时候, 向程序中动态添加业务数据
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CustomProtocolDecoder());
                            pipeline.addLast(new CustomProtocolEncoder());
                            pipeline.addLast(group, "customProtocolServerHandler", new CustomProtocolServerHandler());
                        }
                    });

            // 一个io.netty.channel.Channel代表着一个客户端的连接, 即通道
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            childGroup.shutdownGracefully();
            parentGroup.shutdownGracefully();
        }
    }

    private static class CustomProtocolServerHandler extends SimpleChannelInboundHandler<CustomProtocol> {

        private AtomicLong count = new AtomicLong(0L);

        // 生产中, 为避免业务线程阻塞, 这里通常采用 业务线程池 进行任务调度
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol msg) throws Exception {
            int length = msg.getLength();
            byte[] content = msg.getContent();
            System.out.println("server " + count.incrementAndGet() + " times receive msg, length is: " + length + "; content is: " + new String(content, StandardCharsets.UTF_8));
            // 向客户端返回内容
            byte[] respContent = UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8);
            int respLength = respContent.length;
            CustomProtocol customProtocol = CustomProtocol.builder()
                    .length(respLength)
                    .content(respContent)
                    .build();
            // ChannelHandlerContext ctx
            // ctx.channel().writeAndFlush(customProtocol); 该方式写回的内容会经过所有的 ChannelOutboundHandler
            // 下述方式写回的内容会经过 这个 ctx 之后的其他 ChannelOutBoundHandler
            ctx.writeAndFlush(customProtocol);
        }
    }
}
