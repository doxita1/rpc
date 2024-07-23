package com.doxita.rpcspringbootprovider;

import com.doxita.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class RpcSpringbootProviderApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(RpcSpringbootProviderApplication.class, args);
    }
    
}
