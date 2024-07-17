package com.doxita.rpc.server.tcp;

import com.doxita.rpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxTcpServer implements HttpServer {
    
    private byte[] handleRequest(byte[] requestData) {
        return ("Hello, Client ").getBytes();
    }
    
    /**
     * 启动服务器。
     * 使用Vert.x框架创建一个网络服务器，监听指定的端口，处理进来的连接和数据。
     *
     * @param port 服务器监听的端口号。
     */
    @Override
    public void doStart(int port) {
        // 创建一个Vert.x实例
        Vertx vertx = Vertx.vertx();
        
        // 创建一个网络服务器
        NetServer server = vertx.createNetServer();
        
        // 配置网络服务器的连接处理器
        server.connectHandler(socket -> {
            // 处理客户端发送的数据
            socket.handler(buffer -> {
                // 从Buffer中获取字节数组形式的数据
                byte[] requestData = buffer.getBytes();
                // 处理请求数据，返回响应数据
                byte[] responseData = handleRequest(requestData);
                // 将响应数据写回给客户端
                socket.write(Buffer.buffer(responseData));
            });
        });
        
        // 启动服务器并监听指定端口，处理启动结果
        server.listen(port, result -> {
            if (result.succeeded()) {
                // 服务器启动成功，记录日志
                log.info("TCP Server started on port " + port);
                System.out.println("TCP Server started on port " + port);
            } else {
                // 服务器启动失败，记录错误日志
                log.error("Failed to start TCP server", result.cause());
                System.out.println("Failed to start TCP server" + result.cause());
            }
        });
    }
    
    public static void main(String[] args) {
        VertxTcpServer server = new VertxTcpServer();
        server.doStart(8888);
    }

}
