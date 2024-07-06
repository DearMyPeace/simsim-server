package com.project.simsim_server.exception;

public class DiaryLimitExceededException extends RuntimeException {
    public DiaryLimitExceededException(String message) {
        super(message);
    }
}
