package com.project.simsim_server.exception;

import lombok.Getter;

@Getter
public class CustomRuntimeException extends RuntimeException{
    private final ErrorType errorType;

    public CustomRuntimeException(ErrorType errorType) {
        super(errorType.getMessage());
        this.errorType = errorType;
    }
}
