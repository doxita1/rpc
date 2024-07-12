package com.doxita.rpc.serializer;


import com.doxita.rpc.spi.SpiLoader;

import java.util.HashMap;

public class SerializerFactory {
    //    /**
//     * key:序列化器名称
//     */
//    public static final HashMap<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>();
//
//    /**
//     * 初始化序列化器
//     */
//    static {
//        KEY_SERIALIZER_MAP.put(SerializerKeys.JSON_SERIALIZER, new JsonSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.HESSIAN_SERIALIZER, new HessianSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.KRYO_SERIALIZER, new KryoSerializer());
//        KEY_SERIALIZER_MAP.put(SerializerKeys.JDK_SERIALIZER,new JdkSerializer());
//    }
//
    static {
        SpiLoader.load(Serializer.class);
    }
    
    /**
     * 默认序列化器
     */
    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();
    
    /**
     * 根据key获取序列化器
     *
     * @param key
     * @return
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(Serializer.class,key);
    }
}
