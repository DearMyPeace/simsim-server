package com.project.simsim_server.exception;

public class UserNotFoundException extends RuntimeException {
    private final String errorCode;

    public UserNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
