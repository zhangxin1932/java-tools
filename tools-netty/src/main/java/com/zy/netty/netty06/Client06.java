package com.zy.netty.netty06;

import com.zy.netty.common.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

/**
 * https://www.cnblogs.com/MrRightZhao/p/11967982.html
 * <p>
 * https://www.jianshu.com/p/7052761fa480
 */
@Slf4j
@Getter
public class Client06 {
    private final Bootstrap client;
    private final NioEventLoopGroup nioEventLoopGroup;
    private final int nioThreads;
    private final String host;
    private final int port;
    private final int connectTimeoutMills;
    private final int reconnectTimeoutMills;
    // volatile, please copy reference to use
    private volatile Channel channel;

    public Client06(int nioThreads, String host, int port, int connectTimeoutMills, int reconnectTimeoutMills) {
        this.nioThreads = nioThreads;
        this.host = host;
        this.port = port;
        this.connectTimeoutMills = connectTimeoutMills;
        this.reconnectTimeoutMills = reconnectTimeoutMills;
        this.client = new Bootstrap();
        // FIXME 为了调试, 这里不要通过线程工厂设置 daemon 线程, 如果后续 client 运行在容器(如 tomcat) 中, 这里可以设置为 daemon
        // FIXME 说明下, 如果这里全设置为 daemon, 而下述 bind 端口后又未阻塞主线程, 则所有 daemon 将被销毁, 进行停止. 关于 daemon 线程, 可自行研究
        this.nioEventLoopGroup = new NioEventLoopGroup(nioThreads, new DefaultThreadFactory("client-group", false));
        this.init();
    }

    public Client06(String host, int port) {
        this(Runtime.getRuntime().availableProcessors(), host, port, Constants.CONNECT_TIMEOUT_MILLS, Constants.RECONNECT_TIMEOUT_MILLS);
    }

    private void init() {
        try {
            final WatchdogClientHandler watchdogClientHandler = new WatchdogClientHandler(reconnectTimeoutMills);
            client.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMills)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(4096, Unpooled.copiedBuffer(Constants.CUSTOM_DELIMITER_01.getBytes(StandardCharsets.UTF_8))))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new BusinessClientHandler())
                                    .addLast(new IdleStateHandler(0L, 90L, 0L, TimeUnit.SECONDS))
                                    // FIXME 这里不要 new, 是单例的
                                    .addLast(watchdogClientHandler);
                        }
                    });

            doConnect();
            // FIXME 这行代码的意思是, 当监听到 channel 被关闭时, 关闭自身的连接, 仅做调试用, 不可用于生产
            // channelFuture.channel().closeFuture().sync();
        } catch (Throwable e) {
            System.out.println("client error");
            e.printStackTrace();
        }
    }

    public void doConnect() {
        synchronized (client) {
            try {
                ChannelFuture channelFuture = client.connect(host, port);
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        future.channel().pipeline().fireChannelInactive();
                    } else {
                        Channel newChannel = channelFuture.channel();
                        // copy volatile reference
                        Channel oldChannel = Client06.this.channel;
                        if (Objects.nonNull(oldChannel)) {
                            oldChannel.close();
                        }
                        Client06.this.channel = newChannel;
                    }
                });
            } catch (Exception e) {
                System.out.println("client error");
                e.printStackTrace();
            }
        }
    }

    public Channel getChannel() {
        Channel ch = channel;
        if (Objects.isNull(ch) || !ch.isActive()) {
            return null;
        }
        return ch;
    }

    public void close() {
        if (Objects.nonNull(nioEventLoopGroup)) {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    @ChannelHandler.Sharable
    private static class BusinessClientHandler extends SimpleChannelInboundHandler<Object> {
        private final LongAdder counter = new LongAdder();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            counter.increment();
            System.out.println("client receive msg: " + msg + "; the counter is: " + counter.doubleValue());
        }
    }

    /**
     * 请一定要加上次注解, 关于该注解的作用, 在下文已讲过
     * https://www.jianshu.com/p/051e48410a9d
     */
    @ChannelHandler.Sharable
    private class WatchdogClientHandler extends ChannelInboundHandlerAdapter implements TimerTask {
        private HashedWheelTimer timer;
        private final int timeInternalMills;
        private int retryTimes;

        WatchdogClientHandler(int timeInternalMills) {
            this.timer = new HashedWheelTimer();
            this.timeInternalMills = timeInternalMills;
            this.retryTimes = 0;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event == IdleStateEvent.WRITER_IDLE_STATE_EVENT) {
                    // FIXME 注释掉这里, 测试服务端 close channel 后, 是否重连
                    ctx.writeAndFlush("ping" + Constants.CUSTOM_DELIMITER_01);
                }
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println("连接成功 -- 重置 retryTimes=0");
            retryTimes = 0;
            ctx.fireChannelActive();
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            retryTimes++;
            System.out.println("第" + retryTimes + "次连接失效了");
            // FIXME 这里可以根据 retryTimes 大小判断是否无限重连
            timer.newTimeout(this, timeInternalMills, TimeUnit.MILLISECONDS);
            ctx.fireChannelInactive();
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            System.out.println("开始重连");
            doConnect();
        }
    }
}
