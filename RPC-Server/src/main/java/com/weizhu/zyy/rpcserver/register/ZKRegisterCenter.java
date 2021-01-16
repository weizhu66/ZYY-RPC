package com.weizhu.zyy.rpcserver.register;

import com.weizhu.zyy.rpcserver.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Slf4j
@Component("zookeeper")
public class ZKRegisterCenter implements RegisterCenter{

    @Value("${Zyy.server.ZKUrl:127.0.0.1:2181}")
    private String connectString;

    @Value("${Zyy.server.ZKRootPath:Zyy}")
    private String rootPath;

    private CuratorFramework cf;

    @Override
    public void register(String serviceName, String serverAddress) {
        try {
            if(cf.checkExists().forPath("/" + serviceName) == null){
                cf.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/" + serviceName);
            }

            String path = "/" + serviceName +"/"+ serverAddress;

            cf.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("此服务已存在");
        }
    }

    @PostConstruct
    public void init(){
        cf = CuratorFrameworkFactory.builder().connectString(connectString)
                .sessionTimeoutMs(2000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .namespace(rootPath).build();
        cf.start();
        log.info("zookeeper 连接成功");
    }
}
