package com.doxita.fault.retry;

import com.doxita.rpc.model.RpcResponse;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy{
    /**
     * 对Rpc调用进行重试处理。
     * 当Rpc调用出现异常时，此方法会按照预设的策略进行重试，最大重试次数为3次，每次重试间隔3秒。
     * 通过重试机制，可以提高Rpc调用的稳定性和可靠性。
     *
     * @param callable 要执行的Rpc调用任务，必须是可调用的，并能返回RpcResponse类型的结果。
     * @return Rpc调用的结果，即RpcResponse对象。
     * @throws Exception 如果Rpc调用始终失败，或者在重试过程中发生其他异常，则会抛出异常。
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        // 构建Retryer实例，配置重试策略。
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                .retryIfExceptionOfType(Exception.class) // 指定重试条件为遇到任何异常。
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS)) // 设置固定延迟等待策略，即每次重试间隔3秒。
                .withStopStrategy(StopStrategies.stopAfterAttempt(3)) // 设置重试次数限制，最多重试3次。
                .withRetryListener(new RetryListener() { // 添加重试监听器，用于记录重试信息。
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        log.info("重试次数{}, 重试等待时间{}ms", attempt.getAttemptNumber(), attempt.getDelaySinceFirstAttempt());
                    }
                }).build();
        
        // 使用retryer执行callable任务，如果任务执行成功则返回结果，如果失败则根据重试策略进行重试。
        return retryer.call(callable);
    }

}
