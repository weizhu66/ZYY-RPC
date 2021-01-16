package com.weizhu.zyy.rpcclient.discovery;

import com.weizhu.zyy.rpcclient.balance.LoadBalance;
import com.weizhu.zyy.rpcclient.utils.SpringUtils;
import com.weizhu.zyy.rpcclient.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("zk-discovery")
@Slf4j
public class ZKDiscovery implements ServiceDiscovery {

    private CuratorFramework cf;

    @Value("${Zyy.client.ZKUrl:127.0.0.1:2181}")
    private String connectString;

    @Value("${Zyy.client.ZKRootPath:Zyy}")
    private String rootPath;

    @Value("${Zyy.loadbalance:random}")
    private String loadBalanceAg;

    private volatile Map<String,List<String>> cacheMap = new ConcurrentHashMap<>();
    @Autowired
    SpringUtils springUtils;
    @Override
    public String discover(String serviceName) {
        try {
            List<String> paths = cacheMap.get(serviceName);
            if(paths==null || paths.size() == 0){
                paths = cf.getChildren().forPath("/" + serviceName);
                cacheService(serviceName);

                log.info("从zookeeper中选择服务地址...");
            }else {
                log.info("从客户端缓存中选择服务地址...");
            }

            if(paths == null || paths.size()==0){
                throw new RuntimeException("未找到该服务！");
            }
            LoadBalance loadBalance = (LoadBalance)springUtils.getBean(loadBalanceAg);
            String select = loadBalance.select(paths);

            return select;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    public void cacheService(String serviceName){
        PathChildrenCache cache;
        cache = new PathChildrenCache(cf, "/"+serviceName, true);
        cacheMap.put(serviceName,new ArrayList<>());
        try{
            cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
            cache.getListenable().addListener(new PathChildrenCacheListener() {
                /**
                 * <B>方法名称：</B>监听子节点变更<BR>
                 * <B>概要说明：</B>新建、修改、删除<BR>
                 */
                @Override
                public void childEvent(CuratorFramework cf,
                                       PathChildrenCacheEvent event) throws Exception {
                    switch (event.getType()) {
                        case CHILD_ADDED:
                            cacheMap.get(serviceName)
                                    .add(StringUtils.allPathToAddress(event.getData().getPath()));
                            log.info("CHILD_ADDED :"
                                    + event.getData().getPath());
                            log.info("地址缓存更新:"+cacheMap.toString());
                            break;
                        case CHILD_REMOVED:
                            cacheMap.get(serviceName)
                                    .remove(StringUtils.allPathToAddress(event.getData().getPath()));
                            log.info("CHILD_REMOVED :"
                                    + event.getData().getPath());
                            log.info("地址缓存更新:"+cacheMap.toString());
                            break;
                        default:
                            break;
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
