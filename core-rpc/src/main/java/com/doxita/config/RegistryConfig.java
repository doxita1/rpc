package com.doxita.config;

import lombok.Data;

/**
 * @author: Doxita
 * RPC注册中心配置
 */
@Data
public class RegistryConfig {
    
    /**
     * 注册中心类型
     */
    private String registry = "etcd";
    
    /**
     * 注册中心地址
     */
    private String registryAddress = "http://127.0.0.1:2380";
    
    /**
     * 注册中心用户名
     */
    private String registryUsername = "";
    
    /**
     * 注册中心密码
     */
    private String registryPassword = "";
    
    /**
     * 注册中心超时时间
     */
    private Long registryTimeout = 10000L;
}
