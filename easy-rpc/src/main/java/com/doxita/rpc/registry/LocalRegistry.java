package com.doxita.rpc.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalRegistry {
    /**
     * 存储注册信息
     */
    public static final Map<String, Class<?>> map = new ConcurrentHashMap<>();
    
    /**
     * 服务注册
     * @param serviceName 服务名
     * @param implClass 实现类
     */
    public static void register(String serviceName, Class<?> implClass){
        map.put(serviceName,implClass);
    }
    
    /**
     * 获取服务
     * @param serviceName 服务名
     * @return
     */
    public static Class<?> get(String serviceName){
        return map.get(serviceName);
    }
    
    /**
     * 删除服务
     * @param serviceName 服务名
     */
    public static void remove(String serviceName){
        map.remove(serviceName);
    }
    
}
