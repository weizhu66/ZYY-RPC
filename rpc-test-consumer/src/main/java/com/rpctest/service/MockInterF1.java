package com.rpctest.service;

import com.weizhu.zyy.rpcclient.annotation.Mock;
import org.springframework.stereotype.Service;

@Service
@Mock(interfaceClass = InterF1.class)
public class MockInterF1 implements InterF1 {
    @Override
    public String hello() {
        return "mock hello!";
    }
    @Override
    public String repeat(Integer v) {
        return null;
    }
}
