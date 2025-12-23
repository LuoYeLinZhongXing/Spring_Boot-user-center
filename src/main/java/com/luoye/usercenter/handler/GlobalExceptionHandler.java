package com.luoye.usercenter.handler;


import com.luoye.usercenter.common.Result;
import com.luoye.usercenter.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
//@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BusinessException ex){
        //log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 捕获sql异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        //Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        if(message != null && message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            if (split.length >= 3) {
                String username = split[2];
                // 移除可能包含的引号
                username = username.replaceAll("'", "");
                String msg = "用户名 " + username + " 已存在";
                return Result.error(msg);
            }
        }
        return Result.error("系统错误，请联系管理员");
    }

    /**
     * 捕获所有未处理的运行时异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(RuntimeException ex){
        //log.error("运行时异常：{}", ex.getMessage());
        ex.printStackTrace(); // 打印堆栈跟踪以方便调试
        return Result.error("系统运行时错误，请联系管理员");
    }

    /**
     * 捕获所有未处理的异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(Exception ex){
        //log.error("未捕获的异常：{}", ex.getMessage());
        ex.printStackTrace(); // 打印堆栈跟踪以方便调试
        return Result.error("系统错误，请联系管理员");
    }

}
