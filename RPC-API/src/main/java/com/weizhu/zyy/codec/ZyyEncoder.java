package com.weizhu.zyy.codec;

import com.weizhu.zyy.model.RPCRequest;
import com.weizhu.zyy.model.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ZyyEncoder extends MessageToByteEncoder {
    private ObjectSerializer serializer = new ObjectSerializer();
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(o instanceof RPCRequest){
            byteBuf.writeShort(RPCMessageType.REQUEST.getCode());
        }else if(o instanceof RPCResponse){
            byteBuf.writeShort(RPCMessageType.RESPONSE.getCode());
        }

        byte[] serialize = serializer.serialize(o);
        byteBuf.writeInt(serialize.length);
        byteBuf.writeBytes(serialize);
    }
}
