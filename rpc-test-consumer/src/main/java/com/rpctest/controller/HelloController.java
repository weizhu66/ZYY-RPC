package com.rpctest.controller;

import com.rpctest.service.InterF1;
import com.weizhu.zyy.rpcclient.annotation.ClusterMethod;
import com.weizhu.zyy.rpcclient.client.ZyyClient;
import com.weizhu.zyy.rpcclient.future.FutureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class HelloController {

    @Autowired
    ZyyClient zyyClient;

    @RequestMapping("/t1")
    @ResponseBody
    public String hello() throws ExecutionException, InterruptedException {
//        InterF1 proxy = zyyClient.getProxy(InterF1.class);
//        String hello = proxy.hello();
        FutureService futureService = zyyClient.getFutureService(InterF1.class);
        Future re = futureService.call("repeat",5);
        System.out.println(Thread.currentThread()+" 没有被阻塞！");

        return (String)re.get();
    }

}
