package com.weizhu.zyy.rpcclient.cluster;

import com.weizhu.zyy.model.RPCResponse;
import com.weizhu.zyy.rpcclient.client.NettyClient;
import org.springframework.stereotype.Component;
/*
* 重试
* */
@Component("failOver")
public class FailOverCluster implements Cluster {
    @Override
    public Object invoke(Invocation invocation) {
        int retries = invocation.getRetries();
        NettyClient client = invocation.getClient();
        for (int i = 0; i < retries; i++) {
            try {
                RPCResponse response = client.send(invocation);
                if (response.getCode()==200){
                    return response.getData();
                }else{
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        throw new RuntimeException("重试"+retries+"次，全部失败！");
    }
}
