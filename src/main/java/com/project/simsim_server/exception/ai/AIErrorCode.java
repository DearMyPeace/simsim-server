package com.project.simsim_server.exception.ai;

import com.project.simsim_server.exception.ErrorType;

public enum AIErrorCode implements ErrorType {

    AILETTERS_NOT_FOUND("AI 편지가 존재하지 않습니다.", 400);

    private final String message;
    private final int code;

    AIErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
