package com.doxita.rpcspringbootconsumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class RpcSpringbootConsumerApplicationTests {
    
    @Resource
    private ExampleImpl exampleImpl;
    
    @Test
    void contextLoads() {
        exampleImpl.test();
    }

}
