package com.luoye.usercenter.common.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String message) {
        super(message);
    }
    private BusinessException(){

    }
}
