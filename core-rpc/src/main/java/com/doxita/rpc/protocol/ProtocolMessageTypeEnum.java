package com.doxita.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageTypeEnum {
    
    REQUEST("request", 0),
    RESPONSE("response", 1),
    HEART_BEAT("heartbeat", 2),
    OTHER("other", 4);
    
    private final String text;
    private final int code;
    
    ProtocolMessageTypeEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }
    
    public static ProtocolMessageTypeEnum get(int code) {
        for (ProtocolMessageTypeEnum protocolMessageTypeEnum : ProtocolMessageTypeEnum.values()) {
            if (protocolMessageTypeEnum.code == code) {
                return protocolMessageTypeEnum;
            }
        }
        return OTHER;
    }
}
