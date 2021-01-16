package com.weizhu.zyy.rpcserver.server;

import com.weizhu.zyy.codec.ZyyDecoder;
import com.weizhu.zyy.codec.ZyyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new ZyyDecoder());
        pipeline.addLast(new ZyyEncoder());
        pipeline.addLast(new NettyServerHandler());
    }
}
