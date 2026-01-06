package com.bugmaker.apt.common.exception.handler.advice;

import com.bugmaker.apt.common.exception.handler.ErrorResult;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionCtrAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalArgExHandle(IllegalArgumentException e) {
        log.error("[Exception] ===>  ", e);
        return new ErrorResult(400, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ErrorResult illegalStateHandle(IllegalStateException e) {
        log.error("[Exception] ===>  ", e);
        return new ErrorResult(400, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorResult entityNotFoundHandle(EntityNotFoundException e) {
        log.error("[Exception] ===>  ", e);
        return new ErrorResult(400, e.getMessage());
    }

}
