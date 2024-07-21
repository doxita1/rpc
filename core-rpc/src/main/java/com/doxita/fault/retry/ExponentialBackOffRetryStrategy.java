package com.doxita.fault.retry;

import com.doxita.rpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 指数退避重试策略
 */
@Slf4j
public class ExponentialBackOffRetryStrategy implements RetryStrategy {
    /**
     * 使用重试机制执行Callable任务。
     * 在遇到异常时，根据配置的重试策略进行重试，最多重试3次。重试间隔采用指数增长策略，基础延迟1秒，最大延迟10秒。
     * 该方法旨在提高请求的可靠性和鲁棒性，面对临时故障或延迟可以自动重试，而不是直接失败。
     *
     * @param callable 要执行的Callable任务，该任务应该能够抛出异常并返回RpcResponse类型的结果。
     * @return Callable任务的执行结果，即RpcResponse对象。
     * @throws Exception 如果任务在所有重试尝试后仍然失败，则抛出异常。
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 构建Retryer实例，配置指数退避重试策略
        // todo 指数的有点问题。
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 如果遇到任何异常，则重试。
                .withWaitStrategy(WaitStrategies.exponentialWait(
                        1L, // 基础延迟时间，这里设置为1秒
                        10L, TimeUnit.SECONDS)) // 最大延迟时间，这里设置为10秒，采用指数增长策略。
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 在尝试3次后停止重试。
                .withRetryListener(new RetryListener() { // 注册重试监听器，用于记录重试信息。
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数{}, 重试等待时间{}ms", attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt());
                    }
                })
                .build();
        // 使用retryer执行callable任务，如果失败则根据重试策略进行重试。
        return retryer.call(callable);
        
    }

}
