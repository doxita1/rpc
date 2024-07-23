package com.doxita.fault.retry;

import com.doxita.rpc.spi.SpiLoader;

public class RetryStrategyFactory {
    static {
        SpiLoader.load(RetryStrategy.class);
    }
    
    RetryStrategy DEFAULT_RETRY_STRATEGY = SpiLoader.getInstance(RetryStrategy.class, RetryStrategyKeys.LINEAR_GROWTH);
    public static RetryStrategy getInstance(String key){
        return SpiLoader.getInstance(RetryStrategy.class, key);
    }
}
