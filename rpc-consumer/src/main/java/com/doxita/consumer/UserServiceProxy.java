package com.doxita.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.doxita.RpcApplication;
import com.doxita.common.model.User;
import com.doxita.common.service.UserService;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.serializer.JdkSerializer;
import com.doxita.rpc.serializer.Serializer;
import com.doxita.rpc.spi.SpiLoader;

import java.io.IOException;

public class UserServiceProxy implements UserService {
    @Override
    public User getUser(User user) {
        Serializer serializer = SpiLoader.getInstance(Serializer.class, RpcApplication.getRpcConfig().getSerializer());
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class[]{User.class})
                .args(new Object[]{user})
                .build();
        
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            try(HttpResponse httpResponse = HttpRequest.post("http://localhost:8081")
                    .body(bodyBytes)
                    .execute()){
                result = httpResponse.bodyBytes();
            }
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            return (User)rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
