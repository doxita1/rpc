package com.doxita.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.doxita.RpcApplication;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RpcConfig;
import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.protocol.*;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.rpc.serializer.Serializer;
import com.doxita.rpc.serializer.SerializerFactory;
import com.doxita.rpc.server.tcp.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.doxita.constant.RpcConstant.DEFAULT_SERVICE_VERSION;
import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_MAGIC;
import static com.doxita.rpc.protocol.ProtocolConstant.PROTOCOL_VERSION;

/**
 * jdk动态代理
 */
public class ServiceProxy implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);
    
/**
 * 当代理对象上的方法被调用时，此方法将被调用。
 * 它负责序列化请求，查找服务提供者，建立TCP连接，并发送请求。
 *
 * @param proxy 代理对象，即调用方法的对象。
 * @param method 被调用的方法。
 * @param args 方法的参数。
 * @return 方法的返回值。
 * @throws Throwable 如果调用过程中发生错误。
 */
@Override
public Object invoke(Object proxy, Method method, Object[] args) {
    // 根据配置获取序列化器
    Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
    // 构建RPC请求对象
    // method.getDeclaringClass().getName() 用于获取声明某个方法的类的完全限定名称。
    RpcRequest rpcRequest = RpcRequest.builder()
            .serviceName(method.getDeclaringClass().getName())
            .methodName(method.getName())
            .parameterTypes(method.getParameterTypes())
            .args(args)
            .build();
    try {
        // 序列化RPC请求
        byte[] bodyBytes = serializer.serialize(rpcRequest);
        // 获取RPC配置
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        // 校验是否配置了注册中心
        if (rpcConfig.getRegistryConfig() == null) {
            throw new RuntimeException("未配置注册中心");
        }
        if (rpcConfig.getRegistryConfig().getRegistry() == null) {
            throw new RuntimeException("未配置注册中心");
        }
        // 获取注册中心实例
        // 获取注册中心
        Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
        // 构建服务元数据
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
        serviceMetaInfo.setServiceVersion(DEFAULT_SERVICE_VERSION);
        // 发现服务提供者
        // 发现服务
        List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        // 校验是否有可用的服务提供者
        if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
            throw new RuntimeException("未发现服务");
        }
        // 选择第一个服务提供者
        // 随机选择一个, 选择第一个
        ServiceMetaInfo selectedServiceInfo = serviceMetaInfoList.get(0);
        
        // 发送请求
        RpcResponse rpcResponse = VertxTcpClient.dpRequest(rpcRequest, selectedServiceInfo);
        // 处理响应中的异常
        if (rpcResponse.getException() != null) {
            throw rpcResponse.getException();
        }
        // 返回响应数据
        return rpcResponse.getData();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}
}
