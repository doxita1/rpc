package com.doxita.loadbalancer;

import com.doxita.common.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
public class RandomLoadBalancer implements LoadBalancer{
    
    public static final Random rand = new Random();
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        log.info("RandomLoadBalancer select");
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        int size = serviceMetaInfoList.size();
        if(size == 1){
            return serviceMetaInfoList.get(0);
        }
        int nextInt = rand.nextInt(size);
        log.info("RandomLoadBalancer select nextInt:{}",nextInt);
        return serviceMetaInfoList.get(nextInt);
    }
}
