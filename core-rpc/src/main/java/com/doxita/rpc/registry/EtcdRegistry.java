package com.doxita.rpc.registry;

import cn.hutool.json.JSONUtil;
import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvClient;
    
    /**
     * etcd根路径
     */
    public static final String ETCD_ROOT_PATH = "/rpc/";
    
    
    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder().endpoints(registryConfig.getRegistryAddress())
                .connectTimeout(Duration.ofMillis(registryConfig.getRegistryTimeout()))
                .build();
        kvClient = client.getKVClient();
    }
    
    /**
     * 注册服务
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        
        Lease leaseClient = client.getLeaseClient();
        long leaseId = leaseClient.grant(300).get().getID();
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);
        kvClient.put(key, value, PutOption.newBuilder().withLeaseId(leaseId).build()).get();
    }
    
    /**
     *  注销服务
     * @param serviceMetaInfo 服务信息
     * @throws Exception
     */
    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) throws Exception {
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8)).get();
    }
    
    /**
     * 服务发现
     * @param serviceKey
     * @return
     */
    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            GetOption build = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(ByteSequence.from(searchPrefix, StandardCharsets.UTF_8), build).get().getKvs();
            
            List<ServiceMetaInfo> collect = keyValues.stream()
                    .map(keyValue -> JSONUtil.toBean(keyValue.getValue().toString(StandardCharsets.UTF_8), ServiceMetaInfo.class))
                    .collect(Collectors.toList());
            
            return collect;
        }catch (Exception e){
            throw new RuntimeException("获取服务失败",e);
        }
    }
    
    @Override
    public void destroy() {
        System.out.println("销毁etcd注册中心");
        if (client != null){
            client.close();
        }
        if (kvClient != null){
            kvClient.close();
        }
    }
}
