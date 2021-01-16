package com.weizhu.zyy.rpcserver.server;

import com.weizhu.zyy.rpcserver.register.RegisterCenter;
import com.weizhu.zyy.rpcserver.utils.ScanPackageUtils;
import com.weizhu.zyy.rpcserver.utils.SpringUtils;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.Map;

@Component
@Slf4j
public class ZyyServer implements InitializingBean {

    @Value("${Zyy.server.port}")
    private int port;

    @Value("${Zyy.server.register:zookeeper}")
    private String registerCenterType;

    @Autowired
    private SpringUtils springUtils;


    @Value("${Zyy.server.scanPackage}")
    private String scanPackage;

    public void start(){
        scan();
        init();
    }

    public void scan() {
        try{
            Object obj=springUtils.getBean(registerCenterType);
            if(true){
                RegisterCenter registerCenter=(RegisterCenter) obj;
                //扫描
                Map<String,String> map= ScanPackageUtils.scannerClass(scanPackage);

                //注册
                if(!CollectionUtils.isEmpty(map)){
                    InetAddress addr=InetAddress.getLocalHost();
                    String address = addr.getHostAddress()+":"+port;

                    for(Map.Entry<String, String> entry : map.entrySet()){
                        String className = entry.getKey();
                        String interfaceName = entry.getValue();
                        String serverAddress= className+"&"+ address;

                        registerCenter.register(interfaceName, serverAddress);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void init(){
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer());
            log.info("netty服务器已启动，端口号："+ port);

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 死循环
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
        log.info("ZYY-RPCServer启动完成！");
    }
}
