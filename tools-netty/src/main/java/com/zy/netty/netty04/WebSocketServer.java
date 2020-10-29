package com.zy.netty.netty04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;

/**
 * https://upload-images.jianshu.io/upload_images/7810227-1da1f338fef35035.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
 *
 * https://upload-images.jianshu.io/upload_images/7810227-8fb84471b228b109.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240
 */
@Slf4j
public class WebSocketServer {
    public static void main(String[] args) {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 加入 HTTP 的编解码器
                            pipeline.addLast(new HttpServerCodec());
                            // 加入 块 处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            // 加入http聚合处理器 --> 灰常重要
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            // 加入 服务侧websocket协议 处理器, 其中构造器中websocketPath是指 ws://127.0.0.1:8888/wsp 中端口号后的路径
                            pipeline.addLast(new WebSocketServerProtocolHandler("/wsp"));
                            // 加入自定义handler
                            pipeline.addLast(new TextWebSocketFrameHandler());
                        }
                    });
            ChannelFuture sync = bootstrap.bind(9090).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error.", e);
        } finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }

    /**
     * 自定义 TextWebSocketFrameHandler
     */
    private static class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            System.out.println("server receive msg: " + msg.text());
            // 写回客户端
            ctx.channel().writeAndFlush(new TextWebSocketFrame("server send to client, now time is: " + LocalDateTime.now()));
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            System.out.println("handlerAdded, channelId is : " + ctx.channel().id().asLongText());
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            System.out.println("handlerRemoved, channelId is : " + ctx.channel().id().asLongText());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("there is an exception be caught, channelId is : " + ctx.channel().id().asLongText());
            ctx.close();
        }
    }
}
