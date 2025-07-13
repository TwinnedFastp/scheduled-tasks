package com.itzou.config;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 线程池配置
 *
 * @author itzou
 */
@Configuration
public class SchedulingConfig {
    
    @Value("${task.scheduler.pool-size}")
    private int poolSize;

    @Value("${task.scheduler.thread-name-prefix}")
    private String threadNamePrefix;

    @Value("${task.scheduler.queue-capacity}")
    private int queueCapacity;

    @Value("${task.scheduler.keep-alive-seconds}")
    private int keepAliveSeconds;

    @Value("${task.scheduler.rejected-policy}")
    private String rejectedPolicy;

    @Value("${task.scheduler.wait-for-tasks-to-complete-on-shutdown}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Value("${task.scheduler.await-termination-seconds}")
    private int awaitTerminationSeconds;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix(threadNamePrefix);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        taskScheduler.setAwaitTerminationSeconds(awaitTerminationSeconds);
        
        // 设置拒绝策略
        taskScheduler.setRejectedExecutionHandler(getRejectedExecutionHandler(rejectedPolicy));
        
        return taskScheduler;
    }
    
    private RejectedExecutionHandler getRejectedExecutionHandler(String policy) {
        switch (policy) {
            // 调用者运行策略：由提交任务的线程亲自执行该任务，不允许任务丢失的场景，如数据同步
            case "CALLER_RUNS":
                return new ThreadPoolExecutor.CallerRunsPolicy();
            // 中止策略：直接抛出异常，关键任务，需明确知道任务被拒绝
            case "ABORT":
                return new ThreadPoolExecutor.AbortPolicy();
            // 丢弃策略：不处理，也不抛出异常，不推荐使用，非核心任务，如日志收集
            case "DISCARD":
                return new ThreadPoolExecutor.DiscardPolicy();
            // 丢弃 oldest 策略：丢弃队列中最旧的任务，然后尝试提交新任务，允许任务顺序不严格的场景
            case "DISCARD_OLDEST":
                return new ThreadPoolExecutor.DiscardOldestPolicy();
            default:
                return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    }
}

