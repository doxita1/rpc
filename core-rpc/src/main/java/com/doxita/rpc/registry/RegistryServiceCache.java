package com.doxita.rpc.registry;


import com.doxita.common.model.ServiceMetaInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RegistryServiceCache {

    List<ServiceMetaInfo> serviceCache;
    
    void write(List<ServiceMetaInfo> serviceCache) {
        this.serviceCache = serviceCache;
    }
    
    List<ServiceMetaInfo> read() {
        return serviceCache;
    }
    
    void clear() {
        log.info("清除缓存");
        serviceCache = null;
    }
    
}
