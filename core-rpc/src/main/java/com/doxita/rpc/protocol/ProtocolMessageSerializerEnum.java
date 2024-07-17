package com.doxita.rpc.protocol;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ProtocolMessageSerializerEnum {
    
    JDK(0, "jdk"),
    JSON(1, "json"),
    KRYO(2, "kryo"),
    HESSIAN(3, "hessian");
    
    
    public final String value;
    public final int key;
    
    ProtocolMessageSerializerEnum(int code ,String text) {
        this.value = text;
        this.key = code;
    }
    
    /**
     * 获取所有协议消息序列化器枚举值的字符串表示列表。
     * <p>
     * 该方法通过流操作遍历所有ProtocolMessageSerializerEnum枚举常量，
     * 并将每个枚举常量的值转换为字符串形式，最后收集到一个List中返回。
     * 这样做的目的是为了提供一个方便的方法，来获取所有序列化器的字符串表示，
     * 以便在需要的时候可以方便地访问这些信息。
     *
     * @return 所有协议消息序列化器枚举值的字符串表示列表
     */
    public static List<String> getValues() {
        // 使用流操作遍历枚举常量，并通过map转换每个枚举的值为字符串
        return Arrays.stream(values())
                     .map(ProtocolMessageSerializerEnum::getValue)
                     .collect(Collectors.toList());
    }
    
    /**
     * 根据序列化器的key获取序列化器
     * @param code
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int code) {
        for (ProtocolMessageSerializerEnum value : values()) {
            if (value.key == code) {
                return value;
            }
        }
        return null;
    }
    
    /**
     * 根据序列化器的value获取序列化器
     * @param text
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByValue(String text) {
        if(ObjectUtil.isEmpty(text)){
            return null;
        }
        for (ProtocolMessageSerializerEnum value : values()) {
            if (value.value.equals(text)) {
                return value;
            }
        }
        return null;
    }
}
