package com.doxita.rpc.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_MAGIC;
import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_VERSION;

@Slf4j
public class VertxTcpClient {

    /**
     * 根据提供的RpcRequest和ServiceMetaInfo，通过Vert.x的TCP客户端发送远程过程调用请求，并等待响应。
     *
     * @param rpcRequest RPC请求对象，包含调用的服务方法名和参数等信息。
     * @param serviceMetaInfo 服务元数据信息，包含服务的主机地址和端口号等。
     * @return RpcResponse RPC响应对象，包含调用结果。
     * @throws ExecutionException 如果在Future中完成时抛出异常。
     * @throws InterruptedException 如果线程被中断。
     */
    public static RpcResponse dpRequest(RpcRequest rpcRequest, ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        // 创建Vert.x实例
        Vertx vertx = Vertx.vertx();
        // 创建NetClient实例用于TCP连接
        NetClient netClient = vertx.createNetClient();
        // 创建CompletableFuture用于异步接收RPC响应
        CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();
    
        // 尝试连接服务提供者的主机和端口
        // 连接服务提供者
        netClient.connect(serviceMetaInfo.getServicePort(), "127.0.0.1", result -> {
            if (result.succeeded()) {
                // 连接成功日志
                log.info("TCP连接成功");
                // 获取连接的NetSocket对象
                NetSocket netSocket = result.result();
                // 构建协议消息，包括请求头和请求体
                // 构建协议消息
                ProtocolMessage<RpcRequest> protocolMessage = ProtocolMessage.<RpcRequest>builder()
                        .body(rpcRequest)
                        .header(ProtocolMessage.Header.builder()
                                .magic(PROTOCOL_MAGIC)
                                .version(PROTOCOL_VERSION)
                                .type((byte) ProtocolMessageTypeEnum.REQUEST.getCode())
                                .status((byte) ProtocolMessageStatusEnum.OK.getCode())
                                .serializer((byte) ProtocolMessageSerializerEnum.JDK.getKey())
                                .requestId(IdUtil.getSnowflakeNextId())
                                .build()
                        ).build();
                // 发送请求日志
                log.info("发送请求{}", protocolMessage);
                
                try {
                    // 编码协议消息为Buffer并发送到服务器
                    // 编码协议消息并发送
                    Buffer buffer = ProtocolMessageEncoder.encode(protocolMessage);
                    netSocket.write(buffer);
                    // 发送成功日志
                    log.info("发送成功");
                } catch (IOException e) {
                    // 编码失败日志
                    log.info("编码失败");
                    // 抛出运行时异常
                    throw new RuntimeException("协议消息编码错误");
                }
                
                // 设置NetSocket的处理器，用于接收服务器的响应
                // 处理响应
                TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
                    try {
                        // 解码Buffer为协议消息
                        ProtocolMessage<RpcResponse> responseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                        // 接收到响应日志
                        log.info("收到响应{}", responseProtocolMessage);
                        // 完成CompletableFuture，传递响应体
                        responseFuture.complete(responseProtocolMessage.getBody());
                    } catch (IOException | ClassNotFoundException e) {
                        // 解码失败日志
                        log.info("解码失败");
                        // 抛出运行时异常
                        throw new RuntimeException("协议消息解码错误");
                    }
                });
                // 设置NetSocket的处理器为bufferHandlerWrapper
                netSocket.handler(bufferHandlerWrapper);
            } else {
                // 连接失败日志
                log.info("TCP连接失败");
                log.info("连接失败原因：" + result.cause());
                throw new RuntimeException("TCP连接失败");
            }
        });
        // 等待响应，通过get方法阻塞当前线程直到CompletableFuture完成
        // 等待响应
        RpcResponse rpcResponse = responseFuture.get();
        // 关闭NetClient
        netClient.close();
        // 返回响应对象
        return rpcResponse;
    }
}
