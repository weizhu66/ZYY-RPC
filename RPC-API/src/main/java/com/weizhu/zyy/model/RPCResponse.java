package com.weizhu.zyy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RPCResponse implements Serializable {
    private int code;
    private String message;
    private Object data;
    private Class<?> dataType;
    public static RPCResponse success(Object data){
        return RPCResponse.builder().code(200).message("传输成功")
                                                .data(data)
                                                .dataType(data.getClass())
                                                .build();
    }

    public static RPCResponse fail(){
        return RPCResponse.builder().code(500).message("服务器错误").build();
    }
}
