package com.doxita.rpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class EtcdRegistry implements Registry {
    private Client client;
    private KV kvClient;
    
    /**
     * etcd根路径
     */
    public static final String ETCD_ROOT_PATH = "/rpc/";
    
    /**
     * 本地注册节点key
     */
    public static final Set<String> localRegisterNodeKey = new ConcurrentHashSet<>();
    
    /**
     * 服务缓存
     */
    public static RegistryServiceCache registryServiceCache = new RegistryServiceCache();
    
    /**
     * 监听key
     */
    public static final Set<String> watchingKeySet = new ConcurrentHashSet<>();
    
    
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getRegistryAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getRegistryTimeout()))
                .build();
        kvClient = client.getKVClient();
        // 心跳
        heartBeat();
//        CronUtil.start();
    }
    
    /**
     * 注册服务
     *
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        
        Lease leaseClient = client.getLeaseClient();
        long leaseId = leaseClient.grant(15).get().getID();
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        kvClient.put(key, value, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
        localRegisterNodeKey.add(registerKey);
    }
    
    /**
     * 注销服务
     *
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8)).get();
        localRegisterNodeKey.remove(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());
    }
    
    /**
     * 服务发现
     *
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        // 先从缓存中获取
        List<ServiceMetaInfo> cache = registryServiceCache.read();
        if (CollUtil.isNotEmpty(cache)) {
            log.info("从缓存中获取服务：{}", serviceKey);
            return cache;
        }
        
        
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            // 获取所有key
            GetOption build = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence
                            .from(searchPrefix, StandardCharsets.UTF_8), build)
                    .get()
                    .getKvs();
            
            // 转换为ServiceMetaInfo
            List<ServiceMetaInfo> collect = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听
                        watch(key);
                        log.info("发现服务：{}", key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());
            
            // 缓存
            log.info("缓存服务：{}", JSONUtil.toJsonStr(collect));
            registryServiceCache.write(collect);
            
            return collect;
        } catch (Exception e) {
            throw new RuntimeException("获取服务失败", e);
        }
    }
    
    @Override
    public void destroy() {
        log.info("当前节点下线");
        for (String key : localRegisterNodeKey) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException("注销服务失败", e);
            }
        }
        
        System.out.println("销毁etcd注册中心");
        CronUtil.stop();
        if (client != null) {
            client.close();
        }
        if (kvClient != null) {
            kvClient.close();
        }
    }
    
    @Override
    public void heartBeat() {
        // 每10秒执行一次
        CronUtil.schedule("*/10 * * * * ?", new Task() {
            
            @Override
            public void execute() {
                log.info("执行心跳");
                for (String key : localRegisterNodeKey) {
                    try {
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 如果key不存在或者已过期，则跳过
                        if (CollUtil.isEmpty(keyValues)) {
                            log.info("服务已过期，请重新注册服务");
                            continue;
                        }
                        // 续约
                        KeyValue keyValue = keyValues.get(0);
                        String val = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(val, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                        log.info("续约服务：{}", serviceMetaInfo.getServiceName());
                        
                        
                    } catch (Exception e) {
                        throw new RuntimeException("心跳失败", e);
                    }
                }
            }
        });
        
        CronUtil.setMatchSecond(true);
        CronUtil.start();
        
    }
    
    /**
     * 监视指定的服务节点。
     * 当服务节点发生变化（例如上线或下线）时，更新注册表缓存并记录相应的日志信息。
     *
     * @param serviceNode 要监视的服务节点名称。
     */
    @Override
    public void watch(String serviceNode) {
        // 获取监控客户端
        Watch watchClient = client.getWatchClient();
        // 尝试将新的服务节点添加到正在监视的节点集合中，如果成功则表示这是一个新的监视任务
        // 如果已经监听，则跳过
        boolean newWatch = watchingKeySet.add(serviceNode);
        
        // 如果是新的监视任务
        if (newWatch) {
            // 设置监控，对指定的服务节点进行监视，当节点发生变化时触发回调函数
            watchClient.watch(ByteSequence.from(serviceNode, StandardCharsets.UTF_8), response -> {
                // 遍历响应中的所有事件
                response.getEvents().forEach(event -> {
                    // 根据事件类型采取相应的处理措施
                    switch (event.getEventType()) {
                        case DELETE:
                            // 如果是节点删除事件，清空注册表缓存，并记录服务下线日志
                            registryServiceCache.clear();
                            log.info("服务下线：{}", event.getKeyValue().getValue().toString(StandardCharsets.UTF_8));
                            CronUtil.stop();
                            break;
                        case PUT:
                            // 如果是节点添加或更新事件，记录服务上线日志
                            log.info("服务上线：{}", event.getKeyValue().getValue().toString(StandardCharsets.UTF_8));
                            break;
                        default:
                            // 对于其他类型的事件，目前不做处理
                            break;
                    }
                });
            });
        }
    }
    
    
}
