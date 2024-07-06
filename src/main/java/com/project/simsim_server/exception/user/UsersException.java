package com.project.simsim_server.exception.user;

import com.project.simsim_server.exception.CustomRuntimeException;
import com.project.simsim_server.exception.ErrorType;

public class UsersException extends CustomRuntimeException {
    public UsersException(ErrorType errorType) {
        super(errorType);
    }
}