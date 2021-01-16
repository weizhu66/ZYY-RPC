package com.weizhu.zyy.rpcclient.cluster;

import com.weizhu.zyy.model.RPCRequest;
import com.weizhu.zyy.rpcclient.client.NettyClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
public class Invocation {
    Method method;
    Object[] args;
    NettyClient client;
    int retries = 0;
    Map<String,Object> map;
}
