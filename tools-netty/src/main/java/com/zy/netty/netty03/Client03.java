package com.zy.netty.netty03;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
/**
 * netty的处理器
 *
 * 1.入站处理器
 * 对请求进行拦截, 其顶层是 ChannelInboundHandler
 *
 * 2.出站处理器
 * 对响应进行拦截, 其顶层是 ChannelOutboundHandler
 *
 * 3.数据处理常用的编解码器本质上都是处理器
 *
 * 4.编解码器:
 * 无论向网络中写入的数据是什么类型(int, char等), 最终都是以字节流的方式进行传输的
 * 将数据转为字节流的形式称之为编码(encode)
 * 将字节流转换为对象(int, long, Object)的形式称之为解码(decode)
 *
 * 5.编码本质上是一种出站处理器,ChannelOutboundHandler
 * netty中, 通常以XXXEncoder命名
 *
 * 6.解码本质上是一种入站处理器,ChannelInboundHandler
 * netty中, 通常以XXXDecoder命名
 *
 * 7.无论是编码器还是解码器, 其所接受的数据类型必须要与待处理的参数类型一致, 否则该编码/解码器不会被执行, 也不会报错
 *
 * 8.在解码器进行数据解码时, 一定要记得判断缓冲(ByteBuf)中的数据是否足够, 否则将产生一些严重问题
 */
@Slf4j
public class Client03 {
    public static void main(String[] args) {
        Bootstrap client = new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        try {
            client.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(DataInfo.Message.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new ClientHandler03());
                        }
                    });

            ChannelFuture channelFuture = client.connect("127.0.0.1", 8099).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("client error.", e);
        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    private static class ClientHandler03 extends SimpleChannelInboundHandler<DataInfo.Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DataInfo.Message msg) throws Exception {
            System.out.println("client receive msg: " + msg);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // protobuf 多协议传输的解决方案一
            DataInfo.Message message;
            int i = new Random().nextInt(3);
            switch (i) {
                case 0:
                    message = DataInfo.Message.newBuilder().setDataType(DataInfo.Message.DataType.SchoolType)
                            .setSchool(DataInfo.School.newBuilder().setName("nanjingjinlingzhognxue").setSquare(800).build())
                            .build();
                    break;
                case 1:
                    message = DataInfo.Message.newBuilder().setDataType(DataInfo.Message.DataType.TeacherType)
                            .setTeacher(DataInfo.Teacher.newBuilder().setName("tom").setAge(30).build())
                            .build();
                    break;
                default:
                    message = DataInfo.Message.newBuilder().setDataType(DataInfo.Message.DataType.StuType)
                            .setStu(DataInfo.Stu.newBuilder().setName("jerry").setAge(10).build())
                            .build();
            }
            ctx.writeAndFlush(message);
        }
    }
}
