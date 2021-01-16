package com.weizhu.zyy.rpcclient.future;

import com.weizhu.zyy.rpcclient.annotation.ClusterMethod;
import com.weizhu.zyy.rpcclient.client.NettyClient;
import com.weizhu.zyy.rpcclient.cluster.Cluster;
import com.weizhu.zyy.rpcclient.cluster.Invocation;
import com.weizhu.zyy.rpcclient.utils.SpringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Future;


public class FutureService<T> {

    private Class<?> clazz;

    private NettyClient client;

    private Map<String,Object> map;

    private String defaultCluster = "failFast";

    public FutureService(Class<?> clazz, NettyClient client,Map<String, Object> map) {
        this.clazz = clazz;
        this.client = client;
        this.map = map;
    }

    public Future<Object> call(String methodName, Object... args){
        Method method = null;
        try {
            Class<?> types[] = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName,types);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
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
            return NettyClient.submitTask(()->{
                Object o = cluster.invoke(invocation);
                return o;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };
}
