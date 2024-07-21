package com.doxita.config;

import com.doxita.fault.retry.RetryStrategyKeys;
import com.doxita.loadbalancer.LoadBalancer;
import com.doxita.loadbalancer.LoadBalancerKeys;
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
    private String version = "1.0.0";
    
    /**
     * 是否开启mock
     */
    private boolean mock = false;
    
    /**
     * 序列化方式
     */
    private String serializer;
    
    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();
    
    /**
     * 负载均衡
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    
    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO_RETRY;
}
