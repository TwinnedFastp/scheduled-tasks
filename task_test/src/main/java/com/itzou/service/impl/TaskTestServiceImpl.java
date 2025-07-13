package com.itzou.service.impl;

import org.springframework.stereotype.Service;

/**
 * 定时任务测试
 */
@Service
public class TaskTestServiceImpl {
    public void testPrint1() {
        System.out.println("====== 已启用默认的定时任务1 ======");
    }
}
