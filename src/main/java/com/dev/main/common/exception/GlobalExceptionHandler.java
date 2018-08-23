package com.dev.main.common.exception;

import com.dev.main.common.util.ResultMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResultMap dataNotFoundExceptionHandler() {
        ResultMap errorResult = ResultMap.error();
        return errorResult;
    }

}
