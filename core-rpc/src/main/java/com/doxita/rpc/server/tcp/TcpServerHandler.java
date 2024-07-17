package com.doxita.rpc.server.tcp;

import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.protocol.ProtocolMessage;
import com.doxita.rpc.protocol.ProtocolMessageDecoder;
import com.doxita.rpc.protocol.ProtocolMessageEncoder;
import com.doxita.rpc.protocol.ProtocolMessageTypeEnum;
import com.doxita.rpc.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {
    /**
     * 处理NetSocket的连接和数据传输。
     * 当收到客户端发送的数据时，解码数据以获取RPC请求，然后调用相应的服务方法，并将结果编码回客户端。
     *
     * @param netSocket 与客户端建立的网络套接字，用于数据传输。
     */
    @Override
    public void handle(NetSocket netSocket) {
        // 设置数据处理处理器，对收到的数据进行解码和处理。
        netSocket.handler(buffer -> {
            // 解码接收到的缓冲区数据，转换为协议消息对象。
            ProtocolMessage<RpcRequest> protocolMessage;
            try {
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
                log.info("protocolMessage:{}",protocolMessage);
            } catch (IOException | ClassNotFoundException e) {
                // 解码出错时，抛出运行时异常。
                throw new RuntimeException("协议消息解码错误");
            }
            
            // 提取RPC请求部分。
            RpcRequest rpcRequest = protocolMessage.getBody();
            // 创建一个空的RPC响应对象。
            RpcResponse rpcResponse = new RpcResponse();
            
            try{
                // 从本地注册表中获取请求的服务类。
                Class<?> aClass = LocalRegistry.get(rpcRequest.getServiceName());
                // 获取请求的方法对象。
                Method method = aClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                // 调用方法，并将结果存入RPC响应对象。
                Object result = method.invoke(aClass.newInstance(), rpcRequest.getArgs());
                
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
                log.info("rpcResponse: success{}",rpcResponse);
            }catch (Exception e){
                // 处理方法调用过程中的异常。
                rpcResponse.setException(e);
                rpcResponse.setMessage(e.getMessage());
                log.error("rpcResponse: error{}",rpcResponse);
            }
            
            // 创建响应类型的协议消息头。
            ProtocolMessage.Header header = new ProtocolMessage.Header();
            header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getCode());
            // 将RPC响应包装在协议消息中。
            ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = new ProtocolMessage<>(header,rpcResponse);
            
            try {
                // 编码RPC响应，并写回到客户端。
                netSocket.write(ProtocolMessageEncoder.encode(rpcResponseProtocolMessage));
                log.info("发送响应消息成功{}",ProtocolMessageEncoder.encode(rpcResponseProtocolMessage));
            }catch (IOException e){
                // 发送过程中出现IO异常，记录日志并抛出运行时异常。
                log.error("发送响应消息错误",e);
                throw new RuntimeException("发送响应消息错误");
            }
        });
    }

}
