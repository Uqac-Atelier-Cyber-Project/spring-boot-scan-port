package com.uqac.scan_port;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<String> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException ex, WebRequest request) {
        String errorMessage = "Content-Type '" + ex.getContentType() + "' is not supported. Supported content types are: " + ex.getSupportedMediaTypes();
        return new ResponseEntity<>(errorMessage, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<String> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String errorMessage = "Request method '" + ex.getMethod() + "' is not supported. Supported methods are: " + ex.getSupportedHttpMethods();
        return new ResponseEntity<>(errorMessage, HttpStatus.METHOD_NOT_ALLOWED);
    }
}