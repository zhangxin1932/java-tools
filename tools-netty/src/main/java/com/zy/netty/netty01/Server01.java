package com.zy.netty.netty01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class Server01 {
    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("server-bossGroup", true));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("server-workerGroup", true));

        try {
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // 这里结合 LineBasedFrameDecoder + StringDecoder 实现粘包拆包的处理, 其中 LineBasedFrameDecoder 是基于 \n 或 \r\n 实现
                                    // 这里需要注意: 单条消息不能超过给定的最大限度, 否则会抛出异常
                                    .addLast("lineBasedFrameDecoder", new LineBasedFrameDecoder(1024))
                                    .addLast("stringDecoder", new StringDecoder())
                                    .addLast("serverhandler01", new ServerHandler01());
                        }
                    });

            ChannelFuture channelFuture = server.bind("127.0.0.1", 8099).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server is error ..........", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private static class ServerHandler01 extends SimpleChannelInboundHandler<Object> {

        private LongAdder counter = new LongAdder();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter.increment();

            String body = (String) msg;
            System.out.println("server01 received msg: " + body + "; the counter is: " + counter.doubleValue());

            String currentTime = "Query Time Order".equalsIgnoreCase(body) ? LocalDateTime.now().toString() : "Bad Order";
            // 发送消息时, 每条消息结尾需要添加回车换行符
            currentTime += System.getProperty("line.separator");
            ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(resp);
        }
    }
}
