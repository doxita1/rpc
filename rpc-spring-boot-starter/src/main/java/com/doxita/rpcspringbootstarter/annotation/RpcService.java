package com.doxita.rpcspringbootstarter.annotation;

import com.doxita.constant.RpcConstant;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 服务提供者注解(用于注册服务)
 */
@Target({ java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface RpcService {
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
}
