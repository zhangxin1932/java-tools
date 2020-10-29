package com.zy.netty.netty05;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义协议解决 tcp 传输的粘包 拆包问题
 * 客户端
 */
public class CustomProtocolForPackUnpackClient {
    public static void main(String[] args) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CustomProtocolDecoder());
                            pipeline.addLast(new CustomProtocolEncoder());
                            pipeline.addLast(new CustomProtocolClientHandler());
                        }
                    });

            ChannelFuture localhost = bootstrap.connect("localhost", 8080).sync();
            localhost.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    private static class CustomProtocolClientHandler extends SimpleChannelInboundHandler<CustomProtocol> {
        private AtomicLong count = new AtomicLong(0L);
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, CustomProtocol msg) throws Exception {
            int length = msg.getLength();
            byte[] content = msg.getContent();
            System.out.println("client " + count.incrementAndGet() + " times receive msg, length is: " + length + "; content is: " + new String(content, StandardCharsets.UTF_8));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            int i = 0;
            while (i < 5) {
                String msg = "client " + i + " times send msg to server.";
                byte[] content = msg.getBytes(StandardCharsets.UTF_8);
                int length = content.length;
                CustomProtocol customProtocol = CustomProtocol.builder()
                        .length(length)
                        .content(content)
                        .build();
                ctx.writeAndFlush(customProtocol);
                i++;
            }
        }
    }
}
