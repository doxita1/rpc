package com.doxita.rpc.protocol;

import com.doxita.rpc.serializer.Serializer;
import com.doxita.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;

import java.io.IOException;

/**
 * 编码器,返回buffer
 */
public class ProtocolMessageEncoder {
    /**
     * 编码协议消息。
     *
     * @param protocolMessage 需要编码的协议消息对象。如果对象或其头部为null，则返回一个空的缓冲区。
     * @return 编码后的缓冲区。
     * @throws IOException 如果找不到序列化器，则抛出此异常。
     */
    public static Buffer encode(ProtocolMessage<?> protocolMessage) throws IOException {
        // 检查协议消息及其头部是否为空，如果为空，则返回一个空的缓冲区。
        if (protocolMessage == null || protocolMessage.getHeader() == null){
            return Buffer.buffer();
        }
        
        // 获取协议消息的头部信息。
        ProtocolMessage.Header header = protocolMessage.getHeader();
        // 初始化一个缓冲区用于存储编码后的数据。
        Buffer buffer = Buffer.buffer();
        
        // 将头部信息的各个字段依次追加到缓冲区中。
        buffer.appendByte(header.getMagic());
        buffer.appendByte(header.getVersion());
        buffer.appendByte(header.getType());
        buffer.appendByte(header.getStatus());
        buffer.appendByte(header.getSerializer());
        buffer.appendLong(header.getRequestId());
        
        // 根据头部信息中的序列化器类型，获取对应的序列化器枚举。
        ProtocolMessageSerializerEnum protocolMessageSerializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        // 如果找不到对应的序列化器枚举，则抛出IOException。
        if (protocolMessageSerializerEnum == null){
            throw new IOException("序列化协议不存在:serializer not found");
        }
        
        // 根据序列化器枚举，从序列化器工厂获取具体的序列化器实例。
        Serializer serializer = SerializerFactory.getSerializer(protocolMessageSerializerEnum.getValue());
        // 使用序列化器将消息体序列化为字节数组。
        byte[] bodyBytes = serializer.serialize(protocolMessage.getBody());
        // 追加消息体字节数组的长度到缓冲区中，用于解码时确定消息体的长度。
        buffer.appendInt(bodyBytes.length);
        // 将序列化后的消息体字节数组追加到缓冲区中。
        buffer.appendBytes(bodyBytes);
        
        // 返回编码后的缓冲区。
        return buffer;
        
    }

}
