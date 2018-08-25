package com.dev.main.common.controller;

import com.dev.main.common.exception.DataNotFoundException;
import com.dev.main.common.exception.DataUpdateException;
import com.dev.main.common.util.ResultMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = DataNotFoundException.class)
    public ResultMap dataNotFoundExceptionHandler(DataNotFoundException e) {
        e.printStackTrace();
        return ResultMap.error(e.getMessage());
    }

    @ExceptionHandler(value = DataUpdateException.class)
    public ResultMap dataUpdateExceptionHandler(DataUpdateException e) {
        e.printStackTrace();
        return ResultMap.error(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResultMap exceptionHandler(Exception e) {
        e.printStackTrace();
        return ResultMap.error(e.getMessage());
    }



}
