package com.luoye.usercenter.common;

public class Result<T>  {

    private String message;

    private T data;

    //成功0，失败1
    private int code;

    public Result(String message, T data, int code) {
        this.message = message;
        this.data = data;
        this.code = code;
    }
    public Result(T data, int code) {
        this.data = data;
        this.code = 200;
    }
    public Result(String message, int code) {
        this.message = message;
        this.code = code;
    }


    public Result() {
    }

    // 静态方法，用于创建成功结果
    public static <T> Result<T> success(String message, T data, int code) {
        return new Result<>(message, data, code);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>("success",data, 0); // 使用data和code的构造函数，code为0表示成功
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(message, data, 0); // code为0表示成功
    }

    // 静态方法，用于创建失败结果
    public static <T> Result<T> error(String message) {
        return new Result<>(message, null, 1); // code为1表示失败
    }
    public static <T> Result<T> error(){
        return new Result<>("error",  1);
    }

    public static <T> Result<T> error(String message, T data) {
        return new Result<>(message, data, 1); // code为1表示失败
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(message, null, code);
    }

    // 添加 getter 和 setter 方法
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }



}
