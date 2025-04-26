package com.geology.user.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 忽略null值字段
public class ApiResponse<T> {
    private int errcode; // 错误码
    private String msg;  // 错误消息
    private T body;      // 响应体数据

    // 成功的静态方法
    public static <T> ApiResponse<T> success(T body) {
        return new ApiResponse<>(0, "success", body);
    }

    // 成功的静态方法（自定义消息）
    public static <T> ApiResponse<T> success(T body, String msg) {
        return new ApiResponse<>(0, msg, body);
    }

    // 失败的静态方法
    public static <T> ApiResponse<T> fail(int errcode, String msg) {
        return new ApiResponse<>(errcode, msg, null);
    }

    // 构造函数
    public ApiResponse(int errcode, String msg, T body) {
        this.errcode = errcode;
        this.msg = msg;
        this.body = body;
    }
}
