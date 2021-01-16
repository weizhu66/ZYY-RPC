package com.weizhu.zyy.rpcserver.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.applicationContext == null){
            this.applicationContext  = applicationContext;
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        try{
            return applicationContext.getBean(name);
        }catch(Exception e){
            return null;
        }
    }
}
