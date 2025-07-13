package com.itzou.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class StopTaskRequest {
    @NotBlank(message = "服务名称不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "服务名称必须为小驼峰格式")
    private String serviceName;

    @NotBlank(message = "方法名称不能为空")
    @Pattern(regexp = "^[a-z][a-zA-Z0-9]*$", message = "方法名称必须为小驼峰格式")
    private String serviceMethod;
}