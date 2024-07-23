package com.doxita.rpcspringbootstarter.annotation;

import com.doxita.constant.RpcConstant;
import com.doxita.fault.retry.RetryStrategy;
import com.doxita.fault.retry.RetryStrategyKeys;
import com.doxita.fault.tolerant.TolerantStrategyKeys;
import com.doxita.loadbalancer.LoadBalancerKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务消费者(用于注入服务)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RpcReference {
    /**
     * 服务接口
     * @return
     */
    Class<?> interfaceClass() default void.class;
    
    /**
     * 服务版本
     * @return
     */
    String serviceVersion() default RpcConstant.DEFAULT_SERVICE_VERSION;
    
    /**
     * 负载均衡策略
     * @return
     */
    String loadBalancer() default LoadBalancerKeys.ROUND_ROBIN;
    
    /**
     * 重试机制
     * @return
     */
    String retryStrategy() default RetryStrategyKeys.LINEAR_GROWTH;
    
    /**
     * 容错机制
     * @return
     */
    String tolerantStrategy() default TolerantStrategyKeys.FAIL_SAFE;
    
    /**
     * 是否开启mock
     * @return
     */
    boolean mock() default false;
    
}
