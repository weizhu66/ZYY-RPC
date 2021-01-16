# ZYY-RPC

简单的自开发RPC框架,适合初学学习

1.Netty通信

2.负载均衡

3.支持异步调用

4.支持容错机制

5.Zookeeper注册中心 客户端缓存服务地址

6.简单的注解配置



## Demo

ZYY-Server

```java
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
```

application.properties

```properties
Zyy.server.port = 2001
Zyy.server.scanPackage = com.rpctest.service  //接口所在包
Zyy.server.register = zookeeper
```

ZYY-Client



```java
public interface InterF1 {
    @ClusterMethod(clusterMethod = "failMock",retries = 3)  //容错方法 不加注解默认为failFast
    String hello();
    
    String repeat(Integer v);
}
```

```java
@Controller
public class HelloController {

    @Autowired
    ZyyClient zyyClient;

    @RequestMapping("/t1")
    @ResponseBody
    public String hello() throws ExecutionException, InterruptedException {
        InterF1 proxy = zyyClient.getProxy(InterF1.class);
        String hello = proxy.hello();
  //      FutureService futureService = zyyClient.getFutureService(InterF1.class);
  //      Future re = futureService.call("repeat",5);   //异步调用
  //      return (String)re.get();
        return hello;
    }
}
```

本地伪装方法  RPC调用失败后会调用伪装方法

```java
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
```

