package com.doxita.rpc.registry;

import com.doxita.rpc.spi.SpiLoader;

public class RegistryFactory {
    
    static {
        SpiLoader.load(Registry.class);
    }
    
    /**
     * 默认注册中心
     */
    public static final Registry DEFAULT_REGISTRY = new EtcdRegistry();
    
    /**
     * 获取注册中心
     * @param registry
     * @return
     */
    public static Registry getRegistry(String registry){
        return SpiLoader.getInstance(Registry.class,registry);
    }
    
    
}
