package com.rpctest.config;

import com.weizhu.zyy.rpcserver.server.ZyyServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Conf {
    @Bean
    ZyyServer getBean(){
        return new ZyyServer();
    }
}
