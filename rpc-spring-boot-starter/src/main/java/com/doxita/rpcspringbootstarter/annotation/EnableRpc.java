package com.doxita.rpcspringbootstarter.annotation;

import com.doxita.rpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.doxita.rpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.doxita.rpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用RPC框架的注解。
 * 通过在类上标注此注解，表明该类所在的模块需要启用RPC功能。RPC框架会根据此注解的配置，
 * 加载相应的RPC消费者、初始化程序和提供者等组件，以实现远程过程调用的能力。
 *
 * @Target({ElementType.TYPE}) 指定注解可以应用于类型（类、接口等）上。
 * @Retention(RetentionPolicy.RUNTIME) 指定注解在运行时保留，可以被反射读取。
 * @Import({RpcConsumerBootstrap.class, RpcInitBootstrap.class, RpcProviderBootstrap.class})
 * 指定需要导入的启动类，这些类会在应用启动时被加载和执行，以初始化RPC框架。
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcConsumerBootstrap.class, RpcInitBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {
    /**
     * 是否需要启动RPC服务端。默认为true，即需要启动RPC服务端。
     * 如果设置为false，则只会启动RPC客户端，不会启动服务端。
     *
     * @return 是否需要启动RPC服务端
     */
    boolean needServer() default true;
}

