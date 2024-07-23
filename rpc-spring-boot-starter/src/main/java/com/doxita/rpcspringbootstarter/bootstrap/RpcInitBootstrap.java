package com.doxita.rpcspringbootstarter.bootstrap;

import com.doxita.RpcApplication;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.server.tcp.VertxTcpServer;
import com.doxita.rpcspringbootstarter.annotation.EnableRpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

@Slf4j
public class RpcInitBootstrap implements ImportBeanDefinitionRegistrar {
    /**
     * 注册bean定义的方法，用于在Spring上下文中初始化RPC相关配置和服务器。
     * 当检测到@EnableRpc注解时，此方法将被调用。
     *
     * @param importingClassMetadata 当前注解所在的类的元数据，用于读取@EnableRpc注解的属性。
     * @param registry Spring的bean定义注册表，用于注册bean定义。
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 从@EnableRpc注解中获取是否需要启动RPC服务器的配置
        boolean needServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRpc.class.getName()).get("needServer");
        
        // 初始化RPC应用，加载配置
        RpcApplication.init();
        // 获取RPC配置
        final RpcConfig rpcConfig= RpcApplication.getRpcConfig();
        
        // 根据配置决定是否启动RPC服务器
        if (needServer){
            // 创建并启动RPC服务器
            VertxTcpServer vertxTcpServer = new VertxTcpServer();
            vertxTcpServer.doStart(rpcConfig.getServerPort());
            log.info("Rpc server is started");
        }else{
            log.info("Rpc server is not started");
        }
    }
}
