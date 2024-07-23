package com.doxita.rpc.server.tcp;

import com.doxita.rpc.server.HttpServer;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.parsetools.RecordParser;
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
//        server.connectHandler(socket -> {
//            log.info("New client connected");
//            // 创建RecordParser，用于解析数据包,先解析请求头, 请求头长度固定
//            RecordParser recordParser = RecordParser.newFixed(8);
//            recordParser.setOutput(new Handler<>() {
//                int size = -1;
//                Buffer resultBuffer = Buffer.buffer();
//
//                /**
//                 * 处理缓冲区数据。
//                 * 根据size的状态，决定是初始化size还是处理常规数据。
//                 * 当size为-1时，表示需要从当前buffer中读取size的值，
//                 * 并调整解析器的模式以适应数据大小。
//                 * 当size不为-1时，表示已经开始处理数据，将buffer追加到结果缓冲区中，
//                 * 并在数据处理完成后，重置size和结果缓冲区，为下一次数据处理做准备。
//                 *
//                 * @param buffer 输入的缓冲区，包含可能的数据大小信息或实际数据。
//                 */
//                @Override
//                public void handle(Buffer buffer) {
//                    if (size == -1) {
//                        // 从buffer中读取数据大小，用于后续的数据解析
//                        size = buffer.getInt(0);
//                        log.info("buffer.getInt(0)={}, size = {}", buffer.getInt(0), size);
//                        // 根据读取到的数据大小，调整解析器的模式
//                        recordParser.fixedSizeMode(size);
//                        // 将当前buffer追加到结果缓冲区中
//                        resultBuffer.appendBuffer(buffer);
//                    } else {
//                        // 将当前buffer追加到结果缓冲区中
//                        resultBuffer.appendBuffer(buffer);
//                        // 打印结果缓冲区的内容，用于调试或日志记录
//                        System.out.println(resultBuffer.toString());
//                        // 重置解析器的模式，以处理下一次数据
//                        recordParser.fixedSizeMode(8);
//                        // 重置size，为下一次读取数据大小做准备
//                        size = -1;
//                        // 重置结果缓冲区，为下一次数据收集做准备
//                        resultBuffer = Buffer.buffer();
//                    }
//                }
//            });
//            socket.handler(recordParser);
//        });
        server.connectHandler(new TcpServerHandler());
        
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
