package com.zy.netty.netty01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;
@Slf4j
public class Client01 {
    public static void main(String[] args) {
        Bootstrap client = new Bootstrap();
        NioEventLoopGroup executors = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new DefaultThreadFactory("client-executor", true));

        try {
            client.group(executors)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("clentLogginHandler", new LoggingHandler(LogLevel.DEBUG))
                                    // 这里结合 LineBasedFrameDecoder + StringDecoder 实现粘包拆包的处理, 其中 LineBasedFrameDecoder 是基于 \n 或 \r\n 实现
                                    // 这里需要注意: 单条消息不能超过给定的最大限度, 否则会抛出异常
                                    .addLast("lineBasedFrameDecoder", new LineBasedFrameDecoder(1024))
                                    .addLast("stringDecoder", new StringDecoder())
                                    .addLast("clienthandler01", new ClientHandler01());
                        }
                    });

            ChannelFuture channelFuture = client.connect("127.0.0.1", 8099).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client is error -----------", e);
        } finally {
            executors.shutdownGracefully();
        }
    }

    private static class ClientHandler01 extends SimpleChannelInboundHandler<Object> {

        private LongAdder counter = new LongAdder();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter.increment();

            String body = (String) msg;
            System.out.println("client receive msg: " + body + "; the counter is: " + counter.doubleValue());
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            String req = "Query Time Order";
            // 发送消息时, 每条消息结尾需要添加回车换行符
            req += System.getProperty("line.separator");
            byte[] reqBytes = req.getBytes(StandardCharsets.UTF_8);
            ByteBuf buf;
            for (int i = 0; i < 50; i++) {
                buf = Unpooled.buffer(reqBytes.length);
                buf.writeBytes(reqBytes);
                ctx.writeAndFlush(buf);
            }
        }
    }
}
