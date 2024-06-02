package com.doxita.rpc.serializer;

import java.io.*;

/**
 * Jdk序列化器
 */
public class JdkSerializer implements Serializer {
    /**
     * 序列化对象为字节数组
     *
     * @param object 需要序列化的对象
     * @param <T>    对象的类型
     * @return 序列化后的字节数组
     * @throws IOException 如果发生I/O错误
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        // 创建字节数组输出流，用于存储序列化后的字节
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 创建对象输出流，将对象写入字节数组输出流
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        // 序列化对象
        objectOutputStream.writeObject(object);
        // 关闭对象输出流
        objectOutputStream.close();
        // 返回字节数组输出流中的字节数组
        return byteArrayOutputStream.toByteArray();
    }
    
    /**
     * 反序列化字节数组为对象
     *
     * @param data  需要反序列化的字节数组
     * @param clazz 对象的类类型
     * @param <T>   对象的类型
     * @return 反序列化后的对象
     * @throws IOException            如果发生I/O错误
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException, ClassNotFoundException {
        // 创建字节数组输入流，用于读取字节数组
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        // 创建对象输入流，从字节数组输入流读取对象
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        // 读取对象并转换为指定类型
        T object = clazz.cast(objectInputStream.readObject());
        // 关闭对象输入流
        objectInputStream.close();
        // 返回反序列化后的对象
        return object;
    }
}
