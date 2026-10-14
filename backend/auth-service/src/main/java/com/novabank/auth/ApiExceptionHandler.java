package com.novabank.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handle(IllegalArgumentException ex) { return new ErrorResponse(ex.getMessage()); }
    public record ErrorResponse(String message) {}
}
