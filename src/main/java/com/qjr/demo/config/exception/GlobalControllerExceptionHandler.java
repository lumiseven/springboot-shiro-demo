package com.qjr.demo.config.exception;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)//response状态码
    @ExceptionHandler(AuthorizationException.class)//捕捉的Shiro异常
    public Result AuthorizationExceptionHandler(AuthorizationException e) {
        Result result = new Result(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "权限不足");
        return result;
    }

}
