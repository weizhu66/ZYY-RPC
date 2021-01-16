package com.weizhu.zyy.rpcclient.balance;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component(value = "random")
public class RandomBalance implements LoadBalance{
    @Override
    public String select (List<String> lists){
        int len=lists.size();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return lists.get(random.nextInt(len));
    }
}
