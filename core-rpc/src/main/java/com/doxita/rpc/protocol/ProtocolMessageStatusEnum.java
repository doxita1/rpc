package com.doxita.rpc.protocol;

import lombok.Getter;

@Getter
public enum ProtocolMessageStatusEnum {
    OK("ok", 20),
    BAD_REQUEST("badRequest", 40),
    BAD_RESPONSE("badResponse", 50);
    
    public final String text;
    public final int code;
    
    ProtocolMessageStatusEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }
    
    /**
     * 根据code获取枚举
     * @param code
     * @return
     */
    public static ProtocolMessageStatusEnum get(int code) {
        for (ProtocolMessageStatusEnum statusEnum : values()) {
            if (statusEnum.code == code) {
                return statusEnum;
            }
        }
        return null;
    }
    
}
