package com.itzou;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


/**
 * 定时任务主启动类
 * @author itzou
 */
@SpringBootApplication
@Slf4j
public class TimeTasksApplication {
    public static void main(String[] args) {
        SpringApplication.run(TimeTasksApplication.class, args);
        log.info("*************************** 定时任务启动成功 ********************************");
    }

}
