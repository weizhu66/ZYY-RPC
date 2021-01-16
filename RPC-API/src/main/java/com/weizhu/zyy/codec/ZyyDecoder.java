package com.weizhu.zyy.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ZyyDecoder extends ByteToMessageDecoder {
    private ObjectSerializer serializer = new ObjectSerializer();

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        short messageType = byteBuf.readShort();
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        Object o = serializer.deserialize(bytes);
        list.add(o);
    }
}
