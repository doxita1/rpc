package com.doxita.loadbalancer;

import com.doxita.common.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器
 */
public interface LoadBalancer {
    /**
     * 选择服务
     * @param requestParams 请求参数
     * @param serviceMetaInfoList   服务列表
     * @return 具体服务
     */
    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
