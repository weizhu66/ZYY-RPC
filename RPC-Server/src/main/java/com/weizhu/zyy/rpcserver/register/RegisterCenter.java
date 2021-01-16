package com.weizhu.zyy.rpcserver.register;

import java.net.InetSocketAddress;

public interface RegisterCenter {

    void register(String serviceName, String serverAddress);
}
