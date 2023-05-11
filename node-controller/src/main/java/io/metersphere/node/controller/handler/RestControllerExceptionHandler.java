package io.metersphere.node.controller.handler;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultHolder msExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        return ResultHolder.error(e.getMessage());
    }
}
