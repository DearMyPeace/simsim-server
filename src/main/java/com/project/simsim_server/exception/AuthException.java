package com.project.simsim_server.exception;

public class AuthException extends CustomRuntimeException {
    public AuthException(ErrorType errorType) {
        super(errorType);
    }
}

