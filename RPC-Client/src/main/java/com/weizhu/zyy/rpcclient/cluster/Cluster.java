package com.weizhu.zyy.rpcclient.cluster;

public interface Cluster {
    Object invoke(Invocation invocation);
}
