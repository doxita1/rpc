package com.doxita.fault.retry;

import com.doxita.rpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 配置线性退避重试策略
 */
@Slf4j
public class LinearGrowthRetryStrategy implements RetryStrategy{
    /**
     * 使用重试机制执行给定的Callable任务。
     * 在遇到异常时，会按照线性退避策略重试三次，每次重试的间隔时间逐渐增加。
     * 重试机制有助于在临时故障或不稳定的情况下确保请求的可靠性。
     *
     * @param callable 要执行的Callable任务，它应该能够抛出异常并返回RpcResponse。
     * @return Callable任务的执行结果，即RpcResponse对象。
     * @throws Exception 如果任务在重试次数用尽后仍然失败，则抛出异常。
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 构建Retryer实例，配置线性退避重试策略。
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 设置任何异常都应重试。
                .retryIfExceptionOfType(Exception.class)
                // 配置线性等待策略，初始延迟1秒，每次重试后延迟时间增加1秒。
                .withWaitStrategy(WaitStrategies.incrementingWait(
                        1L, TimeUnit.SECONDS, // 初始延迟
                        1L, TimeUnit.SECONDS)) // 每次重试增加的延迟
                // 设置重试次数限制为3次。
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 添加重试监听器，用于记录重试次数。
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数{}, 重试等待时间{}ms", attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt());
                    }
                })
                // 构建并返回Retryer实例。
                .build();
        // 使用retryer执行callable任务。
        return retryer.call(callable);
    }

}
