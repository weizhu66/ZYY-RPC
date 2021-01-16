package com.rpctest.service;

import com.weizhu.zyy.rpcclient.annotation.ClusterMethod;

public interface InterF1 {
    @ClusterMethod(clusterMethod = "failMock",retries = 3)
    String hello();

    String repeat(Integer v);
}
