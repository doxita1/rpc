package com.doxita.rpc.server;

import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.registry.LocalRegistry;
import com.doxita.rpc.serializer.JdkSerializer;
import com.doxita.rpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {
    
    /**
     * Http请求处理
     * @param httpServerRequest
     */
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        final Serializer serializer = new JdkSerializer();
//        log.info("received request: {},{}",httpServerRequest.method(),httpServerRequest.uri());
        httpServerRequest.bodyHandler(body ->{
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            RpcResponse rpcResponse = new RpcResponse();
            if(rpcRequest == null){
                rpcResponse.setMessage("RPC request is null");
                doResponse(httpServerRequest,rpcResponse,serializer);
                return;
            }
            
            try {
//                通过反射调用对应的服务实现类
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("OK");
                
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
                throw new RuntimeException(e);
            }
            
            doResponse(httpServerRequest,rpcResponse,serializer);
        });
    }
    
    /**
     * 响应
     * @param httpServerRequest
     * @param rpcResponse
     * @param serializer
     */
    private void doResponse(HttpServerRequest httpServerRequest, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = httpServerRequest.response()
                .putHeader("content-type", "application/json");
        
        try {
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            httpServerResponse.end(Buffer.buffer());
            throw new RuntimeException(e);
        }
        
    }
}
