package com.weizhu.zyy.rpcclient.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Map;

@Component
public class SpringUtils implements ApplicationContextAware {
    public static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(this.applicationContext == null){
            this.applicationContext  = applicationContext;
        }
    }

    //通过name获取 Bean
    public static Object getBean(String name){
        try{
            return applicationContext.getBean(name);
        }catch(Exception e){
            return null;
        }
    }

    //通过注解获取 Bean
    public Map<String, Object> scanAnnotation(Class<? extends Annotation> clazz){
        if(clazz.isAnnotation()){
            Map<String, Object> objectMap = applicationContext.getBeansWithAnnotation(clazz);
            return objectMap;
        }

        return null;
    }
}
