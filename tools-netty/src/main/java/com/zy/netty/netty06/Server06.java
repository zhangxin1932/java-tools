package com.zy.netty.netty06;

import com.zy.netty.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@Getter
public class Server06 {
    private final ServerBootstrap server;
    private final NioEventLoopGroup acceptorGroup;
    private final NioEventLoopGroup ioGroup;
    private final int acceptorThreads;
    private final int ioThreads;
    private final String host;
    private final int port;

    public Server06(int acceptorThreads, int ioThreads, String host, int port) {
        this.acceptorThreads = acceptorThreads;
        this.ioThreads = ioThreads;
        this.host = host;
        this.port = port;
        this.server = new ServerBootstrap();
        // FIXME 为了调试, 这里不要通过线程工厂设置 daemon 线程, 如果后续 client 运行在容器(如 tomcat) 中, 这里可以设置为 daemon
        // FIXME 说明下, 如果这里全设置为 daemon, 而下述 bind 端口后又未阻塞主线程, 则所有 daemon 将被销毁, 进行停止. 关于 daemon 线程, 可自行研究
        this.acceptorGroup = new NioEventLoopGroup(acceptorThreads, new DefaultThreadFactory("server-acceptor-group", false));
        this.ioGroup = new NioEventLoopGroup(ioThreads, new DefaultThreadFactory("server-io-group", true));
        this.start();
    }

    public Server06(String host, int port) {
        this(1, Runtime.getRuntime().availableProcessors(), host, port);
    }

    private void start() {
        try {
            server.group(acceptorGroup, ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(4096, Unpooled.copiedBuffer(Constants.CUSTOM_DELIMITER_01.getBytes(StandardCharsets.UTF_8))))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new IdleStateHandler(180L, 0L, 0L, TimeUnit.SECONDS))
                                    .addLast(new BusinessServerHandler());
                        }
                    });

            server.bind(host, port).syncUninterruptibly();
            // FIXME 这里一直监听 client 连接; 也可以用死循环阻塞线程如 while(true){}; 如果 boss 线程都不为 daemon, 则此处也不用再设置
            // channelFuture.channel().closeFuture().sync();
        } catch (Throwable e) {
            System.out.println("server error.");
            e.printStackTrace();
        }
    }

    public void close() {
        if (Objects.nonNull(acceptorGroup)) {
            acceptorGroup.shutdownGracefully();
        }
        if (Objects.nonNull(ioGroup)) {
            ioGroup.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class BusinessServerHandler extends SimpleChannelInboundHandler<Object> {
        private final LongAdder counter = new LongAdder();
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter.increment();
            System.out.println("server receive msg: " + msg + "; the counter is: " + counter.doubleValue());
            // FIXME 这里是 ctx.channel()?
            ctx.writeAndFlush(LocalDateTime.now().toString() + Constants.CUSTOM_DELIMITER_01);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event == IdleStateEvent.READER_IDLE_STATE_EVENT) {
                    System.out.println(String.format("there is a long time to receive msg from client [%s]", ctx.channel().remoteAddress().toString()));
                    // FIXME 这里是用 ctx.close() 还是 ctx.disconnect() ? 二者区别?
                    ctx.close();
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
