package com.doxita.rpc.registry;

import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j

public class ZooKeeperRegistry implements Registry {
    /**
     * 客户端
     */
    private CuratorFramework client;
    /**
     * 发现服务
     */
    private ServiceDiscovery<ServiceMetaInfo> serviceDiscovery;
    
    /**
     * 缓存
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    
    /**
     * 本地注册的节点
     */
    private final Set<String> localRegisterNodeKey = new HashSet<>();
    
    /**
     * 监听的节点
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();
    
    /**
     * zookeeper根路径
     */
    public static final String ZOOKEEPER_ROOT_PATH = "/rpc/zk";
    
    /**
     * 初始化注册中心客户端和服务发现。
     * 使用CuratorFrameworkFactory创建Curator客户端，配置连接字符串和重试策略。
     * 使用ServiceDiscoveryBuilder构建服务发现实例，配置客户端、基础路径和序列化器。
     * 启动客户端和服务发现。如果启动失败，记录错误并抛出RuntimeException。
     *
     * @param registryConfig 注册中心配置，包含注册地址和超时时间。
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        // 创建Curator客户端，配置连接字符串和重试策略。
        client = CuratorFrameworkFactory
                .builder()
                .connectString(registryConfig.getRegistryAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getRegistryTimeout()),3))
                .build();
        
        // 构建服务发现实例，配置客户端、基础路径和序列化器。
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMetaInfo.class)
                .client(client)
                .basePath(ZOOKEEPER_ROOT_PATH)
                .serializer(new JsonInstanceSerializer<>(ServiceMetaInfo.class))
                .build();
        
        try{
            // 启动客户端和服务发现。
            client.start();
            serviceDiscovery.start();
        }catch (Exception e){
            // 记录初始化失败的错误并抛出RuntimeException。
            log.error("初始化失败", e);
            throw new RuntimeException(e);
        }
    }

    
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        serviceDiscovery.registerService(buildServiceInstance(serviceMetaInfo));
        log.info("注册服务: {}", serviceMetaInfo.getServiceKey());
        String registerKey = ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey();
        localRegisterNodeKey.add(registerKey);
    }
    
    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        serviceDiscovery.unregisterService(buildServiceInstance(serviceMetaInfo));
        log.info("注销服务: {}", serviceMetaInfo.getServiceKey());
        localRegisterNodeKey.remove(ZOOKEEPER_ROOT_PATH + "/" + serviceMetaInfo.getServiceNodeKey());
    }
    
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 读取缓存
        List<ServiceMetaInfo> cacheServiceMetaInfoList = registryServiceCache.read();
        if (cacheServiceMetaInfoList != null) {
            log.info("读取缓存");
            return cacheServiceMetaInfoList;
        }
        try {
            Collection<ServiceInstance<ServiceMetaInfo>> serviceInstances = serviceDiscovery.queryForInstances(serviceKey);
            if (serviceInstances != null && !serviceInstances.isEmpty()) {
                
                List<ServiceMetaInfo> serviceMetaInfoList = serviceInstances
                        .stream()
                        .map(ServiceInstance::getPayload)
                        .collect(Collectors.toList());
                log.info("服务发现: {}", serviceMetaInfoList);
                registryServiceCache.write(serviceMetaInfoList);
                log.info("缓存服务: {}", serviceMetaInfoList);
                return serviceMetaInfoList;
            }
        } catch (Exception e) {
            log.error("服务发现失败", e);
            throw new RuntimeException(e);
        }
        return null;
    }
    
    @Override
    public void destroy() {
    
    }
    
    @Override
    public void heartBeat() {
    
    }
    
    @Override
    public void watch(String serviceNode) {
        String serviceNodeKey = ZOOKEEPER_ROOT_PATH + "/" + serviceNode;
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            CuratorCache curatorCache = CuratorCache.build(client, serviceNodeKey);
            curatorCache.start();
            curatorCache.listenable().addListener(CuratorCacheListener.builder()
                    .forDeletes(event -> {
                        registryServiceCache.clear();
                        log.info("服务节点删除: {}", serviceNodeKey);
                    })
                    .forChanges((oldData, newData) -> {
                        registryServiceCache.clear();
                        log.info("服务节点改变: {}", serviceNodeKey);
                    }).build()
            );
        }
        
    }
    
    private ServiceInstance<ServiceMetaInfo> buildServiceInstance(ServiceMetaInfo serviceMetaInfo) {
        String serviceAddress = serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort();
        
        try {
            return ServiceInstance.<ServiceMetaInfo>builder()
                    .id(serviceAddress)
                    .name(serviceMetaInfo.getServiceKey())
                    .address(serviceAddress)
                    .payload(serviceMetaInfo)
                    .build();
        } catch (Exception e) {
            log.error("创建实例失败", e);
            throw new RuntimeException(e);
        }
        
    }
}
