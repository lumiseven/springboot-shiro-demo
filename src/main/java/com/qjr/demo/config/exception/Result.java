package com.qjr.demo.config.exception;

import lombok.Data;

@Data
public class Result {

    public Result(String code, String message) {
        this.code = code;
        this.message = message;
    }

    //错误编码
    private String code;
    //错误信息
    private String message;

}
