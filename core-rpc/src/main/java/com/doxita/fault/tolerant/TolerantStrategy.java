package com.doxita.fault.tolerant;

import com.doxita.rpc.model.RpcResponse;

import java.util.Map;

/**
 * 容错
 */
public interface TolerantStrategy {
    /**
     * 容错
     * @param context 上下文
     * @param e
     * @return
     */
    RpcResponse doTolerant(Map<String, Object> context, Exception e);
}
