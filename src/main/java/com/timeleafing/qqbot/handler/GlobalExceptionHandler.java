package com.timeleafing.qqbot.handler;

import com.timeleafing.qqbot.common.response.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        return R.failedWithMsg(e.getMessage());
    }

}
