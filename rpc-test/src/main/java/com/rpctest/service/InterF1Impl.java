package com.rpctest.service;

import com.weizhu.zyy.rpcserver.anonotation.ZyyRPC;

@ZyyRPC(interfaceClass = InterF1.class)
public class InterF1Impl implements InterF1{
    @Override
    public String hello() {
        return "ok";
    }

    @Override
    public String repeat(Integer v) {
        return String.valueOf(v);
    }
}
