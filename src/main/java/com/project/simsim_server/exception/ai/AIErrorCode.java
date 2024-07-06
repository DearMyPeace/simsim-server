package com.project.simsim_server.exception.ai;

import com.project.simsim_server.exception.ErrorType;

public enum AIErrorCode implements ErrorType {

    AILETTERS_NOT_FOUND("AI 편지가 존재하지 않습니다.", 400),
    AIRESPONE_NOT_FOUND("AI 응답이 존재하지 않습니다.", 400),
    AI_MAIL_FAIL("AI 편지 생성이 실패했습니다.", 404),
    AI_NOT_INVALID_DATE("AI를 생성할 수 없는 날짜입니다.", 400),
    NOT_MEET_USER_GRADE("현재 등급으로 조회할 수 없습니다. 내일 다시 시도해주세요.", 403);

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
