package com.doxita.rpc.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpClient {
    /**
     * 启动客户端以连接到服务器。
     * 该方法创建一个Vert.x实例，并使用该实例创建一个网络客户端，尝试连接到本地主机的8888端口。
     * 如果连接成功，它将向服务器发送一条消息，并设置一个处理器以接收服务器发送的数据。
     * 如果连接失败，它将记录错误信息。
     */
    public void start() {
        // 创建一个Vert.x实例
        Vertx vertx = Vertx.vertx();
        
        // 创建一个网络客户端，并尝试连接到服务器
        vertx.createNetClient().connect(8888, "127.0.0.1", result -> {
            // 连接成功时的操作
            if (result.succeeded()) {
                // 记录连接成功的日志信息
                log.info("Connected!,{}", result.result());
                // 获取连接的套接字
                NetSocket socket = result.result();
                // 向服务器发送消息
                socket.write("Hello from client");
                // 设置数据接收处理器
                socket.handler(data -> {
                    // 输出从服务器接收的数据
                    System.out.println("Got data from server: " + data.toString("ISO-8859-1"));
                    // 记录从服务器接收的数据的日志信息
                    log.info("Got data from server: {}", data.toString("ISO-8859-1"));
                });
            } else {
                // 连接失败时的操作
                // 记录连接失败的日志信息
                log.error("Failed to connect: {}", result.cause());
                // 输出连接失败的信息
                System.out.println("Failed to connect: " + result.cause());
            }
        });
    }

    public static void main(String[] args) {
        VertxTcpClient vertxTcpClient = new VertxTcpClient();
        vertxTcpClient.start();
    }
}
