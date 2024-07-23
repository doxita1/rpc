package com.doxita.common.model;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMetaInfo {
    
    /**
     * 服务名称
     */
    private String serviceName;
    
    /**
     * 服务版本
     */
    private String serviceVersion = "1.0.0";
    
    /**
     * 服务地址
     */
    private String serviceHost = "127.0.0.1";
    
    /**
     * 服务端口
     */
    private Integer servicePort;
    
    /**
     * 服务分组
     */
    private String serviceGroup = "default";
    
    /**
     * 服务键名
     * @return
     */
    public String getServiceKey(){
        return String.format("%s:%s", serviceName, serviceVersion);
    }
    
    /**
     * 服务节点键名
     * @return
     */
    public String getServiceNodeKey(){
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }
    
    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost, "http")){
            serviceHost = String.format("http://%s", serviceHost);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
    
}
