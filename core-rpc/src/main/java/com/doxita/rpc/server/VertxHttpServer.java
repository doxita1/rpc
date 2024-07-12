package com.doxita.rpc.server;

import io.vertx.core.Vertx;

/**
 * vertx Http服务器
 */

public class VertxHttpServer implements HttpServer {
    public void doStart(int port) {
        Vertx vertx = Vertx.vertx();

//        创键http服务器
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        server.requestHandler(new HttpServerHandler());
        
//        server.requestHandler(request -> {
//            System.out.println("received request " + request.method() + " " + request.uri());
//            request.response()
//                    .putTrailer("content-type", "text/plain")
//                    .end("hello from vertx");
//        });
        
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("listening: " + port);
            } else {
                System.out.println("filed: " + result.cause());
            }
        });
        
    }
}
