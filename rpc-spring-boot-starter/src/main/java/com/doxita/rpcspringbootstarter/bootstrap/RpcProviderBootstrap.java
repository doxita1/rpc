package com.doxita.rpcspringbootstarter.bootstrap;

import com.doxita.RpcApplication;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.registry.LocalRegistry;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.rpcspringbootstarter.annotation.RpcService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class RpcProviderBootstrap implements BeanPostProcessor {
    /**
     * 在bean初始化后进行处理。主要用于检测bean上是否标注了RpcService注解，如果存在，则将该bean注册为RPC服务。
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
        // 尝试获取bean类上的RpcService注解
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        
        // 如果bean上标注了RpcService注解
        if (rpcService != null) {
            // 获取RpcService注解中指定的接口类，如果未指定，则取bean实现的第一个接口
            Class<?> interfaceClass = rpcService.interfaceClass();
            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }
            // 从RpcService注解中获取服务名称和服务版本
            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService.serviceVersion();
            
            // 将服务信息注册到本地注册表中
            LocalRegistry.register(serviceName, beanClass);
            // 获取RPC配置
            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            
            // 根据RPC配置获取注册中心实例
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
            
            // 创建服务元信息对象，并设置服务名称、版本、主机和端口
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceVersion(serviceVersion);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            try {
                // 将服务元信息注册到注册中心
                registry.register(serviceMetaInfo);
            } catch (Exception e) {
                // 如果注册过程中出现异常，抛出运行时异常，并附带错误信息
                throw new RuntimeException(serviceName + ":服务注册失败", e);
            }
        }
        // 继承BeanPostProcessor接口的默认处理方法
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

}
