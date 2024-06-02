package com.doxita.provider;

import com.doxita.rpc.server.HttpServer;
import com.doxita.rpc.server.VertxHttpServer;


public class EasyProviderExample {
    public static void main(String[] args) {
        HttpServer httpServer = new VertxHttpServer();
        httpServer.doStart(8082);
    }
}
