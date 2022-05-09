package com.microservices.demo.elastic.query.service.common.api.error.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ElasticQueryServiceErrorHandler
{
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handle(AccessDeniedException accessDeniedException)
    {
        log.error("Access denied exception!", accessDeniedException);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to access this resource!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handle(IllegalArgumentException illegalArgumentException)
    {
        log.error("Illegal argument exception!", illegalArgumentException);
        return ResponseEntity.badRequest().body("Illegal argument exception! " + illegalArgumentException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handle(RuntimeException runtimeException)
    {
        log.error("Service runtime exception!");
        return ResponseEntity.badRequest().body("Service runtime exception! " + runtimeException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handle(Exception exception)
    {
        log.error("Internal server error!");
        return ResponseEntity.internalServerError().body("A server error occurred!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException e)
    {
        log.error("Method argument validation exception!", e);
        final Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}

