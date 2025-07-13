package com.itzou.service.impl;

import jakarta.annotation.PreDestroy;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 任务调度器
 *
 * @author Zou
 */
@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class TaskScheduler2 {

    private static final String INVALID_CRON = "0 0 0 30 2 ?"; // 永不触发的合法表达式
    private final ApplicationContext applicationContext;  // 注入Spring上下文
    private final ThreadPoolTaskScheduler taskScheduler;// 注入线程池任务调度器
    private ScheduledFuture<?> scheduledFuture;// 用于存储定时任务的Future对象
    private final Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>(); // 用于存储多个定时任务

    @SneakyThrows
    public void startScheduledTask(String cronExpression, String serviceName, String serviceMethod) {
        log.debug("尝试启动定时任务，Cron表达式: {}, 服务名: {}, 方法名: {}", cronExpression, serviceName, serviceMethod);

        // 验证Cron表达式是否有效
        String validCron = CronExpression.isValidExpression(cronExpression) ? cronExpression : INVALID_CRON;
        // 获取指定的服务实例
        Object service = applicationContext.getBean(serviceName);
        // 获取任务名称，避免任务名冲突
        String taskKey = generateTaskKey(serviceName, serviceMethod);
        // 如果该任务已经存在，取消之前的任务
        if (taskMap.containsKey(taskKey)) {
            log.warn("任务已存在，taskKey: {}，取消原有任务", taskKey);
            stopScheduledTask(serviceName, serviceMethod); // 取消现有任务
        }
        // 获取指定的Service类名和方法名
        Runnable task = getRunnable(serviceMethod, service);
        // 创建并启动定时任务
        scheduledFuture = taskScheduler.schedule(task, new CronTrigger(validCron));
        // 将任务添加到任务管理器前做空校验
        if (scheduledFuture != null) {
            taskMap.put(taskKey, scheduledFuture);
            log.info("定时任务已更新，使用表达式：{}，taskKey: {}", validCron, taskKey);
        } else {
            log.error("任务调度失败，service: {} method: {}", serviceName, serviceMethod);
        }
        log.debug("定时任务启动尝试完成，Cron表达式: {}, 服务名: {}, 方法名: {}", cronExpression, serviceName, serviceMethod);
    }

    // 生成任务唯一标识（可以根据服务名称和方法名称来区分任务）
    private String generateTaskKey(String serviceName, String serviceMethod) {
        return serviceName + "." + serviceMethod;
    }

    private static @NotNull Runnable getRunnable(String serviceMethod, Object service) {
        String serviceClassName = service.getClass().getName();
        // 创建新的任务，使用反射动态调用方法
        return () -> {
            long startTime = System.currentTimeMillis();
            log.debug("定时任务开始执行：{}", serviceMethod);
            try {
                // 执行指定的Service类中的方法
                Class<?> serviceClass = Class.forName(serviceClassName);
                Method method = serviceClass.getMethod(serviceMethod);
                method.invoke(service);
                log.info("定时任务已执行：{}，执行时间: {}ms", serviceMethod, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("定时任务执行失败，执行时间: {}ms", System.currentTimeMillis() - startTime, e);
            }
        };
    }

    // 停止指定任务
    public void stopScheduledTask(String serviceName, String serviceMethod) {
        String taskKey = generateTaskKey(serviceName, serviceMethod);
        ScheduledFuture<?> scheduledFuture = taskMap.remove(taskKey);
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false); // 停止定时任务
            log.info("已取消任务，taskKey: {}", taskKey);
        } else {
            log.warn("没有找到任务，无法取消，taskKey: {}", taskKey);
        }
    }

    // 停止所有任务
    @PreDestroy
    public void stopAllScheduledTasks() {
        for (ScheduledFuture<?> future : taskMap.values()) {
            future.cancel(false); // 停止所有任务
        }
        taskMap.clear(); // 清空任务管理器
        log.info("已取消所有定时任务");
    }
}
