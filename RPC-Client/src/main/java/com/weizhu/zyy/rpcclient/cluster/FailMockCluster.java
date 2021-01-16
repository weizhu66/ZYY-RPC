package com.weizhu.zyy.rpcclient.cluster;

import com.weizhu.zyy.model.RPCResponse;
import com.weizhu.zyy.rpcclient.client.NettyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/*
* 调用本地伪装方法
* */
@Component(value = "failMock")
@Slf4j
public class FailMockCluster implements Cluster {
    @Override
    public Object invoke(Invocation invocation) {
        NettyClient client = invocation.getClient();
        RPCResponse response = client.send(invocation);
        Map<String, Object> mockMap = invocation.getMap();

        if (response.getCode()==200){
            return response.getData();
        }else{
            Method method = invocation.getMethod();
            Object[] args = invocation.getArgs();
            Object o = null;
            try {
                o = method.invoke(mockMap.get(method.getDeclaringClass().getName()), args);
            } catch (IllegalAccessException|InvocationTargetException e) {
                e.printStackTrace();
                log.error("调用失败！");
            }
            log.info("mock方法调用成功！");
            return o;
        }
    }
}
