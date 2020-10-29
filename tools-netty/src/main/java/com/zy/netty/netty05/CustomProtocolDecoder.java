package com.zy.netty.netty05;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 自定义协议的解码器
 */
public class CustomProtocolDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 获取 CustomProtocol 的 length
        int length = in.readInt();
        // 读取 CustomProtocol 的 content
        byte[] content = new byte[length];
        in.readBytes(content);
        // 构造 CustomProtocol 对象
        CustomProtocol customProtocol = CustomProtocol.builder()
                .length(length)
                .content(content)
                .build();
        // 塞进去
        out.add(customProtocol);
    }
}
