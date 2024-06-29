package com.project.simsim_server.exception;

public class OAuthException extends CustomRuntimeException {
    public OAuthException(ErrorType errorType) {
        super(errorType);
    }
}

