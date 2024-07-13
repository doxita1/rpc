package com.doxita.rpc.registry;

import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;

import java.util.List;

/**
 * 注册中心
 */
public interface Registry {
    /**
     * 初始化
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);
    
    /**
     * 注册服务
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;
    
    /**
     * 注销服务
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception;
    
    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);
    
    /**
     * 销毁
     */
    void destroy();
    
}
