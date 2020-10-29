package com.zy.netty.netty05;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
/**
 * 自定义协议的编码器
 */
public class CustomProtocolEncoder extends MessageToByteEncoder<CustomProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, CustomProtocol msg, ByteBuf out) throws Exception {
        // 先写 CustomProtocol 的 length
        out.writeInt(msg.getLength());
        // 再写 CustomProtocol 的 content
        out.writeBytes(msg.getContent());
    }
}
