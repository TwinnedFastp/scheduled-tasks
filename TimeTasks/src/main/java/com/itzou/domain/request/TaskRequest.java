package com.itzou.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class TaskRequest {
    @NotBlank(message = "Cron表达式不能为空")
    @Pattern(regexp = "^[0-9*?/]+\\s+[0-9*?/]+\\s+[0-9*?/]+\\s+[0-9*?/LW]+\\s+[0-9*?/]+\\s+[0-9*?/L#]+$",
            message = "Cron表达式格式错误，正确格式: 秒 分 时 日 月 周")
    private String cronExpression;

    @NotBlank(message = "服务名称不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "服务名称必须为小驼峰格式")
    private String serviceName;

    @NotBlank(message = "方法名称不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "方法名称必须为小驼峰格式")
    private String serviceMethod;
}