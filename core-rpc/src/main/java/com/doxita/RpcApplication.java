package com.doxita;

import com.doxita.config.RegistryConfig;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 维护一个全局配置对象,使用单例模式中的双重检查锁
 */
@Slf4j
public class RpcApplication {

    public static volatile RpcConfig rpcConfig;
    
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc config init success: {}", rpcConfig.toString());
        
        RegistryConfig registryConfig = newRpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("rpc registry init success: {}", registryConfig);
        
        // 优雅关闭, 在jvm关闭时执行
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
        log.info("rpc registry shutdown hook init success");
        
    }
    
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        } catch (Exception e) {
            log.error("rpc config init error: {}", e.getMessage());
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }
    
    /**
     * 获取配置 使用单例模式中的双重检查锁
     * @return rpcConfig
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
