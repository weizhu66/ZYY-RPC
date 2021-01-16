package com.weizhu.zyy.rpcserver.server;

import com.weizhu.zyy.model.RPCRequest;
import com.weizhu.zyy.model.RPCResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<RPCRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest rpcRequest) throws Exception {
        RPCResponse rpcResponse= getResponse(rpcRequest);
        channelHandlerContext.writeAndFlush(rpcResponse).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                log.info("send response success");
            }
        });
        channelHandlerContext.close();
    }

    private RPCResponse getResponse(RPCRequest rpcRequest) {
        String className = rpcRequest.getClassName();
        try {
            Class<?> clazz = Class.forName(className);
            Method method = null;
            method = clazz.getMethod(rpcRequest.getMethodName(),rpcRequest.getParamsType());
            Object instance = clazz.newInstance();
            Object data = method.invoke(instance,rpcRequest.getParams());
            return RPCResponse.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return RPCResponse.fail();
        }
    }

}
