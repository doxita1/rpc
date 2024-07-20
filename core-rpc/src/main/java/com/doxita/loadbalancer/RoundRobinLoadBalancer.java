package com.doxita.loadbalancer;

import com.doxita.common.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡
 */
@Slf4j
public class RoundRobinLoadBalancer implements LoadBalancer{
    /**
     * 当前索引
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        log.info("RoundRobinLoadBalancer select");
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        int size = serviceMetaInfoList.size();
        if (size == 1){
            return serviceMetaInfoList.get(0);
        }
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
