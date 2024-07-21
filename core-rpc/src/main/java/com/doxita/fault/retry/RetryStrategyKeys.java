package com.doxita.fault.retry;

public interface RetryStrategyKeys {
    String NO_RETRY = "noRetry";
    String FIXED_INTERVAL = "fixedInterval";
    String LINEAR_GROWTH = "linearGrowth";
    String EXPONENTIAL_BACK_OFF = "exponentialBackOff";
}
