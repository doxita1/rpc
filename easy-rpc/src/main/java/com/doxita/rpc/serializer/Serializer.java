package com.doxita.rpc.serializer;

import java.io.IOException;

public interface Serializer {
    
    /**
     * 序列化
     * @param object 对象
     * @return 字节数组
     * @param <T> 对象类型
     * @throws IOException
     */
    <T> byte[] serialize(T object) throws IOException;
    
    /**
     * 反序列化
     * @param bytes 字节数组
     * @param type 对象
     * @return
     * @param <T> 对像类型
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> type) throws IOException, ClassNotFoundException;
}
