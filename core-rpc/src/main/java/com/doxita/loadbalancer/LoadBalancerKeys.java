package com.doxita.loadbalancer;

/**
 * 负载均衡器常量
 */

public interface LoadBalancerKeys {
    /**
     * 负载均衡器类型
     */
    String ROUND_ROBIN = "roundRobin";
    String CONSISTENT_HASH = "consistentHash";
    String RANDOM = "random";
}
