package com.konstantinov.onlinestore.rest.advice;

import com.konstantinov.onlinestore.exception.CakeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CakeAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CakeNotFoundException.class)
    public void cakeNotFound(){

    }
}
