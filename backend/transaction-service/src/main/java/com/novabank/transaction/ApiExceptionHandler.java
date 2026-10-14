package com.novabank.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(IllegalArgumentException ex) { return new ErrorResponse(ex.getMessage()); }
    @ExceptionHandler(org.springframework.web.client.RestClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRemote(org.springframework.web.client.RestClientResponseException ex) { return new ErrorResponse(ex.getResponseBodyAsString()); }
    public record ErrorResponse(String message) {}
}
