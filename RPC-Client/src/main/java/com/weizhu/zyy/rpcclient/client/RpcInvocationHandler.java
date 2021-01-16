package com.weizhu.zyy.rpcclient.client;

import com.weizhu.zyy.model.RPCRequest;
import com.weizhu.zyy.model.RPCResponse;
import com.weizhu.zyy.rpcclient.annotation.ClusterMethod;
import com.weizhu.zyy.rpcclient.cluster.Cluster;
import com.weizhu.zyy.rpcclient.cluster.Invocation;
import com.weizhu.zyy.rpcclient.discovery.ServiceDiscovery;
import com.weizhu.zyy.rpcclient.utils.SpringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

@Data
public class RpcInvocationHandler implements InvocationHandler {

    private NettyClient client;

    private Map<String,Object> map;

    @Value("${Zyy.client.defaultCluster:failFast}")
    private String defaultCluster;

    public RpcInvocationHandler(NettyClient client, Map<String, Object> map) {
        this.client = client;
        this.map = map;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        Invocation invocation = Invocation.builder().method(method)
                .args(args).client(client).map(map).build();
        Annotation annotation = method.getAnnotation(ClusterMethod.class);
        String name = null;
        int retries = 0;
        if(annotation==null){
            name = defaultCluster;
        }else{
            ClusterMethod anno = (ClusterMethod) annotation;
            name = anno.clusterMethod();
            retries = anno.retries();
            invocation.setRetries(retries);
        }
        Cluster cluster =(Cluster) SpringUtils.getBean(name);
        if (cluster == null){
            throw new RuntimeException("容错方法错误");
        }
        try {
            Object o = cluster.invoke(invocation);
            return o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
