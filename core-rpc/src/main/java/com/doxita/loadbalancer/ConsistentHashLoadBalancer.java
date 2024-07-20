package com.doxita.loadbalancer;

import cn.hutool.core.util.HashUtil;
import com.doxita.common.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class ConsistentHashLoadBalancer implements LoadBalancer{
    
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();
    
    public static final int VIRTUAL_NODES = 100;
    /**
     * 根据请求参数和服务元信息列表，选择一个合适的服务元信息。
     * 如果服务元信息列表为空，返回null。
     * 如果列表中只有一个服务元信息，直接返回该信息。
     * 对于多个服务元信息的情况，将每个服务元信息的虚拟节点信息存入虚拟节点映射表中。
     * 根据请求参数的哈希值，在虚拟节点映射表中找到合适的节点，并返回对应的服务元信息。
     *
     * @param requestParams 请求参数，用于选择服务。
     * @param serviceMetaInfoList 服务元信息列表，包含多个可选的服务。
     * @return 返回选中的服务元信息，如果没有合适的，则返回null。
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        log.info("ConsistentHashLoadBalancer select");
        
        // 如果服务元信息列表为空，返回null
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 如果服务元信息列表只有一个元素，直接返回该元素
        if (serviceMetaInfoList.size() == 1){
            return serviceMetaInfoList.get(0);
        }
        // 遍历服务元信息列表，为每个服务添加虚拟节点到虚拟节点映射表中
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList){
            for (int i = 0; i < VIRTUAL_NODES; i++){
                virtualNodes.put(HashUtil.apHash(serviceMetaInfo.getServiceKey() + i), serviceMetaInfo);
            }
        }
        // 根据请求参数的哈希值，计算哈希值
        int hash = HashUtil.apHash(requestParams.toString());
        // 在虚拟节点映射表中找到大于等于哈希值的第一个节点，如果不存在，则取第一个节点
        Map.Entry<Integer, ServiceMetaInfo> entry = virtualNodes.ceilingEntry(hash);
        if (entry == null){
            entry = virtualNodes.firstEntry();
        }
        // 返回选中的服务元信息
        return entry.getValue();
    }

}
