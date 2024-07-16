package com.doxita.rpc.registry;

import com.doxita.common.model.ServiceMetaInfo;
import com.doxita.config.RegistryConfig;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class RegistryTest extends TestCase {
    
    public void setUp() throws Exception {
        super.setUp();
    }
    
    public static final Registry registry = new EtcdRegistry();
    
    @Test
    public void init() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setRegistryAddress("http://localhost:2379");
        registry.init(registryConfig);
    }
    
    @Test
    public void testRegister() {
        init();
        
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(1234);
        serviceMetaInfo.setServiceVersion("1.0.0");
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(12341);
        serviceMetaInfo.setServiceVersion("1.0.0");
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(12345);
        serviceMetaInfo.setServiceVersion("1.0.0");
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(12345);
        serviceMetaInfo.setServiceVersion("1.0.2");
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testUnregister() {
        init();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceHost("127.0.0.1");
        serviceMetaInfo.setServicePort(12345);
        serviceMetaInfo.setServiceVersion("1.0.0");
        try {
            registry.unregister(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testServiceDiscovery() {
        init();
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName("myService");
        serviceMetaInfo.setServiceVersion("1.0.0");
        
        List<ServiceMetaInfo> serviceMetaInfoList =
                registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
        for (ServiceMetaInfo serviceMetaInfo1 : serviceMetaInfoList) {
            System.out.println(serviceMetaInfo1.getServiceAddress());
        }
        
    }
    
    @Test
    public void testDestroy() {
    }
    
    @Test
    public void testHeartBeat() throws InterruptedException {
        testRegister();
        Thread.sleep(100000);
    }
    
}