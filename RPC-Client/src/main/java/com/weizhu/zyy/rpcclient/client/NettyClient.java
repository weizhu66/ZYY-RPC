package com.weizhu.zyy.rpcclient.client;

import com.weizhu.zyy.model.RPCRequest;
import com.weizhu.zyy.model.RPCResponse;
import com.weizhu.zyy.rpcclient.cluster.Invocation;
import com.weizhu.zyy.rpcclient.discovery.ServiceDiscovery;
import com.weizhu.zyy.rpcclient.utils.SpringUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.concurrent.*;

@Component
@Slf4j
public class NettyClient {
    private static  Bootstrap bootstrap;
    private static  EventLoopGroup eventLoopGroup;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000));

    @Value("${Zyy.client.discovery:zk-discovery}")
    private String discoveryType;
    @PostConstruct
    public void init(){
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());

    }
    public RPCResponse doSend(RPCRequest request, String host, int port){
        try {
            ChannelFuture channelFuture  = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse response = channel.attr(key).get();

            log.info(String.valueOf(response.getCode()));
            return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public RPCResponse send(Invocation invocation){
        ServiceDiscovery discovery =(ServiceDiscovery) SpringUtils.getBean(discoveryType);
        Method method = invocation.getMethod();
        String result = discovery.discover(method.getDeclaringClass().getName());
        String[] arrs = result.split("&");
        String className = arrs[0];
        String host=arrs[1].split(":")[0];
        int port=Integer.parseInt(arrs[1].split(":")[1]);
        RPCRequest request = RPCRequest.builder().className(className)
                .methodName(method.getName()).params(invocation.getArgs()).paramsType(method.getParameterTypes())
                .build();
        return doSend(request,host,port);
    }
    public static Future<Object> submitTask(Callable r){
        return threadPoolExecutor.submit(r);
    }
}
