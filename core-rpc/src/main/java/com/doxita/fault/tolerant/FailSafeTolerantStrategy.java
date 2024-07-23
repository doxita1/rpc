package com.doxita.fault.tolerant;

import com.doxita.rpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        log.error("FailSafeTolerantStrategy: {}", e.getMessage());
        return RpcResponse.builder()
                .exception(e)
                .message(e.getMessage())
                .build();
    }
}
