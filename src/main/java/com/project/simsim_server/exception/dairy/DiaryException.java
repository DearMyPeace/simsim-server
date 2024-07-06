package com.project.simsim_server.exception.dairy;

import com.project.simsim_server.exception.CustomRuntimeException;
import com.project.simsim_server.exception.ErrorType;

public class DiaryException extends CustomRuntimeException {
    public DiaryException(ErrorType errorType) {
        super(errorType);
    }
}

