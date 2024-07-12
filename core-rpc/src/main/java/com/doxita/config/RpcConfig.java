package com.doxita.config;

import com.doxita.rpc.serializer.SerializerKeys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class RpcConfig {
    /**
     *  服务名称
     */
    private String name = "rpc";
    
    /**
     * 服务端口
     */
    private Integer serverPort = 8082;
    
    /**
     * 服务地址
     */
    private String serverHost = "127.0.0.1";
    
    /**
     * 版本号
     */
    private String version = "1.0";
    
    /**
     * 是否开启mock
     */
    private boolean mock = false;
    
    /**
     * 序列化方式
     */
    private String serializer;
}
