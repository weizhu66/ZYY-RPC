package com.weizhu.zyy.rpcclient.balance;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Component(value = "round")
public class RoundBalance implements LoadBalance {

    private AtomicInteger pos = new AtomicInteger(0);
    @Override
    public String select(List<String> lists) {
        int len = lists.size();
        int index = pos.getAndAdd(1) % len;
        return lists.get(index);
    }
}
