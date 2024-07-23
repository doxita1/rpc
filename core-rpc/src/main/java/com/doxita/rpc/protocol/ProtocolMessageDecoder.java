package com.doxita.rpc.protocol;

import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.serializer.Serializer;
import com.doxita.rpc.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
@Slf4j
public class ProtocolMessageDecoder {
    
    /**
     * 解码协议消息。
     * 从给定的Buffer中解析出协议头和消息体，并根据序列化协议和消息类型反序列化消息体。
     *
     * @param buffer 包含协议消息的Buffer。
     * @return 解析后的协议消息对象。
     * @throws IOException 如果在读取Buffer时发生IO错误。
     * @throws ClassNotFoundException 如果在反序列化时找不到类定义。
     */
    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException, ClassNotFoundException {
        // 初始化协议头
        ProtocolMessage.Header header = new ProtocolMessage.Header();
        // todo 读取并校验魔法数
//        byte magic = buffer.getByte(0);
//
//        if (magic != ProtocolConstant.PROTOCOL_MAGIC){
//            throw new RuntimeException("magic error");
//        }
        
        // 从Buffer中读取协议头的各个字段
        header.setMagic(buffer.getByte(0));
        header.setVersion(buffer.getByte(1));
        header.setType(buffer.getByte(2));
        header.setStatus(buffer.getByte(3));
        header.setSerializer(buffer.getByte(4));
        header.setRequestId(buffer.getLong(5));
        log.info("协议头: {}",header);
        // 读取消息体长度
        // 解决粘包问题
        header.setBodyLength(buffer.getInt(13));
        log.info("消息体长度: {}",header.getBodyLength());
        // 从Buffer中读取消息体
        byte[] bodyBytes = buffer.getBytes(17, 17 + header.getBodyLength());
        // 根据序列化协议获取序列化器
        ProtocolMessageSerializerEnum serializerEnum = ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer());
        if (serializerEnum == null){
            throw new RuntimeException("序列化协议不存在: serializer error");
        }
        Serializer serializer = SerializerFactory.getSerializer(serializerEnum.getValue());
        // 根据消息类型获取消息类型枚举
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.get(header.getType());
        if (messageTypeEnum == null){
            throw new RuntimeException("消息类型不存在: type error");
        }
        
        // 根据消息类型反序列化消息体并返回相应的协议消息对象
        switch (messageTypeEnum){
            case REQUEST:
                log.info("请求消息");
                RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                log.info("请求消息: {}",rpcRequest);
                return new ProtocolMessage<>(header,rpcRequest);
            case RESPONSE:
                log.info("响应消息");
                RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                log.info("响应消息: {}",rpcResponse);
                return new ProtocolMessage<>(header,rpcResponse);
            case HEART_BEAT:
            case OTHER:
                // 目前未处理的心跳消息和其他类型消息
                // todo 其他类型
                log.info("其他类型");
                throw new RuntimeException("其他类型");
//                return new ProtocolMessage<>(header,null);
//                break;
        }
        return null;
    }
}
