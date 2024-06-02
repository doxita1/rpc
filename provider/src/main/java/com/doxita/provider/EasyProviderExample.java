package com.doxita.provider;

import com.doxita.common.service.UserService;
import com.doxita.rpc.registry.LocalRegistry;
import com.doxita.rpc.server.HttpServer;
import com.doxita.rpc.server.VertxHttpServer;


public class EasyProviderExample {
    public static void main(String[] args) {
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        System.out.println(LocalRegistry.map);
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8082);
    }
}
