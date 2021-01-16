package com.weizhu.zyy.rpcclient.balance;

import java.util.List;

public interface LoadBalance {
    public String select(List<String> lists);
}
