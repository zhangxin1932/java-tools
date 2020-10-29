package com.zy.netty.netty03;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Server03 {
    public static void main(String[] args) {
        ServerBootstrap server = new ServerBootstrap();
        // server 这里的 boss, worker 都没有设置 nThreads, 走默认值: io.netty.channel.MultithreadEventLoopGroup.DEFAULT_EVENT_LOOP_THREADS
        // 1.当 worker 不存在, server.group(boss, boss) 是 Reactor 的单线程模型
        // 2.当 worker 存在, boss 的 nThreads == 1 时,  server.group(boss, worker) 是 Reactor 的多线程模型
        // 3.当 worker 存在, boss 的 nThreads > 1 时,  server.group(boss, worker) 是 Reactor 的主从线程模型
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(new DefaultThreadFactory("bossGroupExecutor", true));
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(new DefaultThreadFactory("bossGroupExecutor", true));

        try {
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    // ProtobufVarint32FrameDecoder 用于解决粘包拆包问题
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    // ProtobufDecoder 仅仅支持解码, 不支持半包处理
                                    // 这个解码器定义了要解码的数据类型: 这里限制了只能解码Student.Stu类型, 思考解决方案, 如何通用?
                                    .addLast(new ProtobufDecoder(DataInfo.Message.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new ServerHandler03());
                        }
                    });

            ChannelFuture channelFuture = server.bind("127.0.0.1", 8099).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error.", e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 这里讲述下 @Sharable 的作用:
     * 1.一般情况下, Server 每与 Client 建立一个连接, 都会建立一个 Channel, 一个 ChannelPipeline
     * 在每一个 ChannelPipeline 中都会新建 ChannelHandler, 如: ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
     *
     * 2.如果要想定义一个全局的 ChannelHandler (单例) 供所有的 Channel 使用, 比如统计全局的 metrics 信息, 则可以:
     * ServerHandler03 sharableHandler = new ServerHandler03(); // 这个定义在 server.group 之前
     * server.group(boss, worker).
     * ... // 这里 加入
     * ch.pipeline().addLast(sharableHandler);
     *
     * 3.需要说明的是: 第2步中的定义的 sharableHandler 必须被 @Sharable 修饰, 否则, 当有第二个 Channel 建立时, 将会报错, 详见:
     * io.netty.channel.DefaultChannelPipeline#checkMultiplicity(io.netty.channel.ChannelHandler)
     *
     * 4.当然, 如果只是每建立一个 Channel, 就 new 一个 ChannelHandler, 则 @Sharable 修饰与否 都不影响
     */
    @ChannelHandler.Sharable
    private static class ServerHandler03 extends SimpleChannelInboundHandler<DataInfo.Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DataInfo.Message msg) throws Exception {
            // protobuf 多协议传输的解决方案一
            if (msg.getDataType() == DataInfo.Message.DataType.SchoolType) {
                System.out.println("server received msg: " + msg.getSchool());
            } else if (msg.getDataType() == DataInfo.Message.DataType.TeacherType) {
                System.out.println("server received msg: " + msg.getTeacher());
            }  else if (msg.getDataType() == DataInfo.Message.DataType.StuType) {
                System.out.println("server received msg: " + msg.getStu());
            }
        }
    }
}
