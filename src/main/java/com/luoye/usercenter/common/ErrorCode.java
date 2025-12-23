package com.luoye.usercenter.common;

/**
 * 错误码
 */
public enum ErrorCode {


    SUCCESS(0, "ok", ""),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, " 参数为空", ""),
    NO_AUTH(40010,"无权限",""),
    NO_LOGIN(40020,"未登录","");


    private final int code;
    private final String message;
    private final String description;


     ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }
}
