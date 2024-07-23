package com.doxita.rpcspringbootconsumer;

import com.doxita.rpcspringbootstarter.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class RpcSpringbootConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcSpringbootConsumerApplication.class, args);
    }

}
