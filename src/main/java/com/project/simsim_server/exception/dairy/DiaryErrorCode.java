package com.project.simsim_server.exception.dairy;

import com.project.simsim_server.exception.ErrorType;

public enum DiaryErrorCode implements ErrorType {

    LIMIT_EXCEEDED("금일 작성할 수 있는 일기 갯수를 초과했습니다.", 400);

    private final String message;
    private final int code;

    DiaryErrorCode(String message, int code) {
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
