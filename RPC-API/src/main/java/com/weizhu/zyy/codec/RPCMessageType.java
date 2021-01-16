package com.weizhu.zyy.codec;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum RPCMessageType {
    REQUEST(1),
    RESPONSE(2);
    private int code;
    public int getCode(){
        return code;
    }
}
