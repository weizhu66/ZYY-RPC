package com.weizhu.zyy.rpcclient.client;

import com.weizhu.zyy.rpcclient.annotation.Mock;
import com.weizhu.zyy.rpcclient.discovery.ServiceDiscovery;
import com.weizhu.zyy.rpcclient.future.FutureService;
import com.weizhu.zyy.rpcclient.utils.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZyyClient {

    @Autowired
    private NettyClient client;

    @Value("${Zyy.client.discovery:zk-discovery}")
    private String discoveryType;

    @Autowired
    private SpringUtils springUtils;

    private Map<String,Object> mockMap = new HashMap<>();
    /*
    * 同步调用
    * */
    public <T> T getProxy(Class<?> clazz){
        RpcInvocationHandler handler = new RpcInvocationHandler(client,mockMap);
        Object o = Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz},
                handler);
        return (T)o;
    }
    /*
    * 异步调用
    * */
    public FutureService getFutureService(Class<?> clazz){
        ServiceDiscovery discovery = (ServiceDiscovery)  springUtils.getBean(discoveryType);
        return new FutureService<Object>(clazz,client,mockMap) ;
    }

    @PostConstruct
    private void scanMock(){
        Map<String, Object> objectMap = springUtils.scanAnnotation(Mock.class);
        for (String s : objectMap.keySet()) {
            Object v = objectMap.get(s);
            Mock annotation = v.getClass().getAnnotation(Mock.class);
            if (annotation!=null){
                Class aClass = annotation.interfaceClass();
                mockMap.put(aClass.getName(),v);
            }
        }
    }
}
