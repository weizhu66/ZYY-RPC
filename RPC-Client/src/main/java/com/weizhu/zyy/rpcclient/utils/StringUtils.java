package com.weizhu.zyy.rpcclient.utils;

import java.net.InetSocketAddress;

public class StringUtils {
    // 地址 -> XXX.XXX.XXX.XXX:port 字符串
    public static String getServiceAddress(InetSocketAddress serverAddress) {
        return serverAddress.getHostName() +
                ":" +
                serverAddress.getPort();
    }
    // 字符串解析为地址
    public static InetSocketAddress parseAddress(String address) {
        String[] result = address.split(":");
        return new InetSocketAddress(result[0], Integer.parseInt(result[1]));
    }

    public static String allPathToAddress(String path){
        if (path==null) return null;
        int idx = path.lastIndexOf("/");
        return path.substring(idx+1);
    }
}
