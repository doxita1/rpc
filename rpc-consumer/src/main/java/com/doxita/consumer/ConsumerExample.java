package com.doxita.consumer;

import com.doxita.RpcApplication;
import com.doxita.config.RpcConfig;
import com.doxita.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
