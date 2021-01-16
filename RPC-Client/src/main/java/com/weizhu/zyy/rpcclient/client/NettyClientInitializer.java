package com.weizhu.zyy.rpcclient.client;

import com.weizhu.zyy.codec.ZyyDecoder;
import com.weizhu.zyy.codec.ZyyEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel nioSocketChannel) throws Exception {
        ChannelPipeline pipeline = nioSocketChannel.pipeline();
        pipeline.addLast(new ZyyDecoder());
        pipeline.addLast(new ZyyEncoder());
        pipeline.addLast(new NettyClientHandler());
    }
}
