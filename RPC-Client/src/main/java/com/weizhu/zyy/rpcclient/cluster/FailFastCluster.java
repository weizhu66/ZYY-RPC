package com.weizhu.zyy.rpcclient.cluster;

import com.weizhu.zyy.model.RPCResponse;
import com.weizhu.zyy.rpcclient.client.NettyClient;
import org.springframework.stereotype.Component;

@Component("failFast")
public class FailFastCluster implements Cluster {
    @Override
    public Object invoke(Invocation invocation){
        NettyClient client = invocation.getClient();
        RPCResponse response = client.send(invocation);
        if(response.getCode()==200){
            return response.getData();
        }else {
            throw new RuntimeException("调用失败");
        }
    }
}
