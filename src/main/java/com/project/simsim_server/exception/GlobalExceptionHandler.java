package com.project.simsim_server.exception;

import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.exception.auth.OAuthException;
import com.project.simsim_server.exception.dairy.DiaryException;
import com.project.simsim_server.exception.user.UsersException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<Object> handleOAuthException(OAuthException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        int errorCode = errorType.getCode();
        HttpStatus status = HttpStatus.valueOf(errorCode);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, String.valueOf(errorCode)), status);
    }

    @ExceptionHandler(AIException.class)
    public ResponseEntity<Object> handleAIException(AIException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        int errorCode = errorType.getCode();
        HttpStatus status = HttpStatus.valueOf(errorCode);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, String.valueOf(errorCode)), status);
    }

    @ExceptionHandler(DiaryException.class)
    public ResponseEntity<Object> handleAIException(DiaryException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        int errorCode = errorType.getCode();
        HttpStatus status = HttpStatus.valueOf(errorCode);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, String.valueOf(errorCode)), status);
    }

    @ExceptionHandler(UsersException.class)
    public ResponseEntity<Object> handleAIException(UsersException ex) {
        ErrorType errorType = ex.getErrorType();
        String errorMessage = errorType.getMessage();
        int errorCode = errorType.getCode();
        HttpStatus status = HttpStatus.valueOf(errorCode);
        return new ResponseEntity<>(new ErrorResponse(errorMessage, String.valueOf(errorCode)), status);
    }
}
