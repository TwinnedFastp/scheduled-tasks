package com.itzou.controller;

import com.itzou.domain.request.StopTaskRequest;
import com.itzou.domain.request.TaskRequest;
import com.itzou.domain.result.R;
import com.itzou.service.impl.TaskScheduler2;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 任务调度器控制器 - 改进版本
 * 把需要实现定时任务的模块引入到本项目的POM文件中，然后在本项目中调用即可
 *
 * @author Zou
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/task-scheduler")
@Slf4j
public class TaskSchedulerController {

    private final TaskScheduler2 taskScheduler;


    /**
     * 启动定时任务
     * 示例（body）：
     * {
     *      "cronExpression": "0/15 * * * * ?", Cron表达式，如0/15 * * * * ? 表示每15秒执行一次
     *      "serviceName": "taskTestServiceImpl", // Service实现类的类名（注意：需要小驼峰，如：jobServiceTestImpl）
     *      "serviceMethod": "testPrint1" // Service实现类中的方法名称（注意：需要小驼峰且不能有小括号和参数，如：testPrint1）
     * }
     *
     * @param request 包含cron表达式、服务名称和方法名称的请求对象
     * @return 返回结果
     */
    @PostMapping("/start")
    public R<String> startTask(@Valid @RequestBody TaskRequest request) {
        try {
            taskScheduler.startScheduledTask(
                    request.getCronExpression(),
                    request.getServiceName(),
                    request.getServiceMethod()
            );
            return R.ok("定时任务启动成功：" + request.getServiceName() + "|" + request.getServiceMethod() + "|" + request.getCronExpression());
        } catch (Exception e) {
            log.error("定时任务启动失败", e);
            return R.fail("定时任务启动失败：" + e.getMessage());
        }
    }

    /**
     * 停止定时任务
     * 示例（body）：
     * {
     *      "serviceName": "taskTestServiceImpl", // Service实现类的类名（注意：需要小驼峰，如：jobServiceTestImpl）
     *      "serviceMethod": "testPrint1" // Service实现类中的方法名称（注意：需要小驼峰且不能有小括号和参数，如：testPrint1）
     * }
     *
     * @param request 包含服务名称和方法名称的请求对象
     * @return 返回结果
     */
    @PostMapping("/stop")
    public R<String> stopTask(@Valid @RequestBody StopTaskRequest request) {
        try {
            taskScheduler.stopScheduledTask(
                    request.getServiceName(),
                    request.getServiceMethod()
            );
            return R.ok("定时任务停止成功：" + request.getServiceName() + "|" + request.getServiceMethod());
        } catch (Exception e) {
            log.error("定时任务停止失败", e);
            return R.fail("定时任务停止失败: " + e.getMessage());
        }
    }
}
