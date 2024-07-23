package com.doxita.provider;

import com.doxita.RpcApplication;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.common.service.UserService;
import com.doxita.config.RegistryConfig;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.bootstrap.ProviderBootstrap;
import com.doxita.rpc.model.ServiceRegisterInfo;
import com.doxita.rpc.registry.LocalRegistry;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.rpc.server.HttpServer;
import com.doxita.rpc.server.VertxHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
public class EasyProviderExample {
//    public static void main(String[] args) {
////        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
////        System.out.println(LocalRegistry.map);
////        HttpServer httpServer = new VertxHttpServer();
////        httpServer.doStart(8082);
////        RpcApplication.init();
//
//        String serviceName = UserService.class.getName();
//        LocalRegistry.register(serviceName,UserServiceImpl.class);
//
//        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
//        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
//        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
//
//        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
//        serviceMetaInfo.setServiceName(serviceName);
//        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
//        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
//        serviceMetaInfo.setServiceVersion(rpcConfig.getVersion());
//
//        try {
//            registry.register(serviceMetaInfo);
//            log.info("注册成功{}", serviceMetaInfo);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//
//        HttpServer httpServer = new VertxHttpServer();
//        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
//    }
public static void main(String[] args) {
    ProviderBootstrap providerBootstrap = new ProviderBootstrap();
    List<ServiceRegisterInfo<?>> serviceRegisterInfoList = List.of(new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class));
    providerBootstrap.init(serviceRegisterInfoList);
}
}
