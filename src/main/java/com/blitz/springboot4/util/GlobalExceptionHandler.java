package com.blitz.springboot4.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // 作用于所有 @RestController
public class GlobalExceptionHandler {

    // 处理所有未捕获的异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 500);
        error.put("message", "Internal Server Error");
        error.put("details", ex.getMessage());
        System.out.println(ex.getMessage());
        return error;
    }

    // 处理空指针异常
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleNullPointerException(NullPointerException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", 400);
        error.put("message", "Null Pointer Exception");
        error.put("details", ex.getMessage());
        System.out.println(ex.getMessage());
        return error;
    }


}
