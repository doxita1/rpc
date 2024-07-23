package com.doxita.rpcspringbootstarter.bootstrap;

import com.doxita.rpc.proxy.ServiceProxyFactory;
import com.doxita.rpcspringbootstarter.annotation.RpcReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RpcConsumerBootstrap implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(RpcConsumerBootstrap.class);
    
    /**
     * 在bean初始化后进行处理。
     * 此方法的目的是为标注了RpcReference注解的字段注入代理对象。
     *
     * @param bean 刚被初始化的bean对象。
     * @param beanName bean的名称。
     * @return 处理后的bean对象。
     * @throws BeansException 如果处理过程中出现异常。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取bean的类类型
        Class<?> beanClass = bean.getClass();
        
        // 获取bean类中所有声明的字段
        Field[] declaredFields = beanClass.getDeclaredFields();
        for (Field field : declaredFields) {
            // 检查字段是否标注了RpcReference注解
            RpcReference rpcReference = field.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // 获取RpcReference注解中指定的接口类，如果未指定，则使用字段本身的类型
                Class<?> interfaceClass = rpcReference.interfaceClass();
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }
                // 设置字段可访问，以便后续注入代理对象
                field.setAccessible(true);
                // 创建接口类的代理对象
                Object proxyObject = ServiceProxyFactory.getProxy(interfaceClass);
                try {
                    // 将代理对象注入到字段中
                    field.set(bean, proxyObject);
                    // 取消字段的可访问性设置，恢复原状
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    // 记录注入代理对象失败的日志信息
                    log.info("set field error,注入代理对象失败");
                    // 抛出运行时异常，封装原始异常信息
                    throw new RuntimeException("为字段注入代理对象失败", e);
                }
            }
        }
        
        // 调用父类方法，继续处理bean的初始化
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
