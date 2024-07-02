package com.project.simsim_server.exception.ai;

import com.project.simsim_server.exception.CustomRuntimeException;
import com.project.simsim_server.exception.ErrorType;

public class AIException extends CustomRuntimeException {
    public AIException(ErrorType errorType) {
        super(errorType);
    }
}
