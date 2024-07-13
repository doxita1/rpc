package com.doxita.rpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.doxita.RpcApplication;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RpcConfig;
import com.doxita.constant.RpcConstant;
import com.doxita.rpc.model.RpcRequest;
import com.doxita.rpc.model.RpcResponse;
import com.doxita.rpc.registry.Registry;
import com.doxita.rpc.registry.RegistryFactory;
import com.doxita.rpc.serializer.JdkSerializer;
import com.doxita.rpc.serializer.Serializer;
import com.doxita.rpc.serializer.SerializerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import static com.doxita.constant.RpcConstant.DEFAULT_RPC_PREFIX;
import static com.doxita.constant.RpcConstant.DEFAULT_SERVICE_VERSION;

/**
 * jdk动态代理
 */
public class ServiceProxy implements InvocationHandler {
    /**
     * @param proxy  the proxy instance that the method was invoked on
     * @param method the {@code Method} instance corresponding to
     *               the interface method invoked on the proxy instance.  The declaring
     *               class of the {@code Method} object will be the interface that
     *               the method was declared in, which may be a superinterface of the
     *               proxy interface that the proxy class inherits the method through.
     * @param args   an array of objects containing the values of the
     *               arguments passed in the method invocation on the proxy instance,
     *               or {@code null} if interface method takes no arguments.
     *               Arguments of primitive types are wrapped in instances of the
     *               appropriate primitive wrapper class, such as
     *               {@code java.lang.Integer} or {@code java.lang.Boolean}.
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
//        Serializer serializer = new JdkSerializer();
        Serializer serializer = SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
        //method.getDeclaringClass().getName() 用于获取声明某个方法的类的完全限定名称。
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            if (rpcConfig.getRegistryConfig() == null) {
                throw new RuntimeException("未配置注册中心");
            }
            if (rpcConfig.getRegistryConfig().getRegistry() == null) {
                throw new RuntimeException("未配置注册中心");
            }
            // 获取注册中心
            Registry registry = RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(DEFAULT_SERVICE_VERSION);
            // 发现服务
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (serviceMetaInfoList == null || serviceMetaInfoList.isEmpty()) {
                throw new RuntimeException("未发现服务");
            }
            // 随机选择一个, 选择第一个
            ServiceMetaInfo selectedServiceInfo = serviceMetaInfoList.get(0);
            
            
            try (HttpResponse httpResponse = HttpRequest.post(selectedServiceInfo.getServiceAddress())
                    .body(bodyBytes)
                    .execute()) {
                byte[] result = httpResponse.bodyBytes();
                RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
                return rpcResponse.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
