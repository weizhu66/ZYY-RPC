package com.weizhu.zyy.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RPCRequest implements Serializable {
    private String className;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsType;
}
