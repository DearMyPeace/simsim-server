package com.project.simsim_server.exception;

import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.exception.auth.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<Object> handleOAuthException(OAuthException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        String errorCode = String.valueOf(errorType.getCode());
        HttpStatus status = HttpStatus.valueOf(errorCode);

        return new ResponseEntity<>(new ErrorResponse(errorMessage, errorCode), status);
    }

    @ExceptionHandler(AIException.class)
    public ResponseEntity<Object> handleAIException(AIException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        String errorCode = String.valueOf(errorType.getCode());
        HttpStatus status = HttpStatus.valueOf(errorCode);

        return new ResponseEntity<>(new ErrorResponse(errorMessage, errorCode), status);
    }
}
