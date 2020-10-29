package com.zy.netty.netty02;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.LongAdder;

/**
 * 这里多启动几个 client, 即可测试
 */
@Slf4j
public class Client02 {
    private static final String DELIMITER_SEPARATOR = "$_";
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
                                    // 这里结合 DelimiterBasedFrameDecoder + StringDecoder 实现粘包拆包的处理, 其中 DelimiterBasedFrameDecoder 需要自定义分隔符, 否则走默认值
                                    // 这里需要注意: 单条消息不能超过给定的最大限度, 否则会抛出异常
                                    .addLast("$_delimiterBasedFrameDecoder", new DelimiterBasedFrameDecoder(4096, Unpooled.copiedBuffer(DELIMITER_SEPARATOR.getBytes(StandardCharsets.UTF_8))))
                                    .addLast("stringDecoder", new StringDecoder())
                                    .addLast("stringEncoder", new StringEncoder())
                                    .addLast("clientHandler02", new ClientHandler02());
                        }
                    });

            ChannelFuture channelFuture = client.connect("127.0.0.1", 8090).sync();
            // 死循环, 监听键盘输入, 客户端启动后(可以多启动几个客户端), 输入任意消息, 回车即可
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                channelFuture.channel().writeAndFlush(reader.readLine() + DELIMITER_SEPARATOR);
            }
            // channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("client is error -----------", e);
        } finally {
            executors.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class ClientHandler02 extends SimpleChannelInboundHandler<Object> {
        private LongAdder counter = new LongAdder();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter.increment();

            String body = (String) msg;
            System.out.println("client receive msg: " + body + "; the counter is: " + counter.doubleValue());
        }
    }
}
