package com.doxita.rpc.proxy;

import com.doxita.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂(创键代理对象)
 */
public class ServiceProxyFactory {
    
    /**
     * 根据服务类获取代理对象
     * @param serviceClass
     * @return
     * @param <T>
     */
    /**
     * Proxy.newInstance 方法通常需要以下参数：
     * ClassLoader loader: 类加载器，用于定义代理类。通常，你可以使用目标类的类加载器，例如 target.getClass().getClassLoader()。
     * Class<?>[] interfaces: 这是一个接口数组，代理类将实现这些接口。这允许代理实例作为任何接口的实例被调用，
     *      其方法调用将被转发到你提供的调用处理器。
     * InvocationHandler h: 这是一个实现了 InvocationHandler 接口的实例。此处理器的 invoke 方法将拦截对代理实例的方法调用。
     *      在 RPC 框架中，这个处理器通常负责实现方法调用的网络传输，即将本地调用转换为网络请求。
     */
    // 获取代理
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        
        //确保 serviceClass 是一个接口，因为 Java 动态代理只能代理接口。
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new ServiceProxy()
        );
    }
    
    
    // 获取 Mock 代理
    public static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(
                serviceClass.getClassLoader(),
                new Class[]{serviceClass},
                new MockServiceProxy()
        );
    }
}
