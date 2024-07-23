package com.doxita.rpc.bootstrap;

import com.doxita.RpcApplication;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.model.ServiceRegisterInfo;
import com.doxita.rpc.registry.LocalRegistry;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.rpc.server.tcp.VertxTcpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 服务提供者启动类
 */
@Slf4j
public class ProviderBootstrap {
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfoList){
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfoList) {
            log.info("registering service: {}", serviceRegisterInfo.getServiceName());
            String serviceName = serviceRegisterInfo.getServiceName();
            
            LocalRegistry.register(serviceName, serviceRegisterInfo.getImplClass());
            
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
            
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(serviceName);
            serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
            serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
            serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
            try {
                registry.register(serviceMetaInfo);
                log.info("服务注册成功");
            } catch (Exception e) {
                throw new RuntimeException("服务注册失败",e);
            }
        }
        VertxTcpServer vertxTcpServer = new VertxTcpServer();
        vertxTcpServer.doStart(rpcConfig.getServerPort());
    }
}
