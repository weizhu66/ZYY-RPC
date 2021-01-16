package com.weizhu.zyy.rpcserver.utils;

import com.weizhu.zyy.rpcserver.anonotation.ZyyRPC;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ScanPackageUtils {
    static Map<String,String> classNames=new HashMap<String, String>();

    public static Map<String,String> scannerClass(String packageName){
        try{
            String name=packageName.replaceAll("\\.","/");
            URL url=ScanPackageUtils.class.getClassLoader().getResource(name);
            File file=new File(url.getFile());
            File[] files=file.listFiles();
            for(File f:files){
                if(f.isDirectory()){
                    scannerClass(packageName+"."+f.getName());
                }else{
                    String classname=packageName+"."+f.getName().replace(".class", "");
                    Class clazz=Class.forName(classname);
                    Annotation anno=clazz.getAnnotation(ZyyRPC.class);
                    if(anno!=null){
                        ZyyRPC rpcAnno=(ZyyRPC) anno;
                        Class interfaceClass=rpcAnno.interfaceClass();
                        classNames.put(classname, interfaceClass.getName());

                    }
                }
            }
            return classNames;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
