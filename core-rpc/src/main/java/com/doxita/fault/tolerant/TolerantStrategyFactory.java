package com.doxita.fault.tolerant;

import com.doxita.rpc.spi.SpiLoader;

public class TolerantStrategyFactory {
    
    static {
        SpiLoader.load(TolerantStrategy.class);
    }
    
    private static final TolerantStrategy DEFAULT_TOLERANT_STRATEGY = new FailSafeTolerantStrategy();
    
    public static TolerantStrategy getInstance(String key) {
        return SpiLoader.getInstance(TolerantStrategy.class, key);
    }
}
