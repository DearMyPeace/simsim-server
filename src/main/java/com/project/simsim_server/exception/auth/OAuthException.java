package com.project.simsim_server.exception.auth;

import com.project.simsim_server.exception.CustomRuntimeException;
import com.project.simsim_server.exception.ErrorType;

public class OAuthException extends CustomRuntimeException {
    public OAuthException(ErrorType errorType) {
        super(errorType);
    }
}

