package com.doxita.rpc.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProtocolMessage<T> {
    /**
     * 消息头
     */
    private Header header;
    
    /**
     * 消息体
     */
    private T body;
    
    /**
     * 消息头
     */
   
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Header {
        /**
         * 魔术
         */
        private byte magic;
        /**
         * 消息版本
         */
        private byte version;
        /**
         * 序列化方式
         */
        private byte serializer;
        /**
         * 消息类型(请求/响应)
         */
        private byte type;
        /**
         * 状态
         */
        private byte status;
        /**
         * 请求id
         */
        private long requestId;
        /**
         * 消息体长度
         */
        private int bodyLength;
        
    }
    

}
