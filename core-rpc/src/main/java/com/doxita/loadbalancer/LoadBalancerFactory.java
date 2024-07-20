package com.doxita.loadbalancer;

import com.doxita.rpc.spi.SpiLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoadBalancerFactory {
    static {
        SpiLoader.load(LoadBalancer.class);
    }
    
    /**
     * 默认的负载均衡器
     */
    private static final LoadBalancer DEFAULT_LB = new RoundRobinLoadBalancer();
    
    /**
     * 获取实例
     * @param key
     * @return
     */
    public static LoadBalancer getInstance(String key) {
        LoadBalancer instance = SpiLoader.getInstance(LoadBalancer.class, key);
        log.info("LoadBalancerFactory getInstance:{}", instance.toString());
        return instance;
    }

}
