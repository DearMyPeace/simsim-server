package com.project.simsim_server.exception.user;

import com.project.simsim_server.exception.ErrorType;

public enum UsersErrorCode implements ErrorType {

    CANCLE_ACCOUNT("이미 탈퇴한 회원입니다.", 404),
    USER_NOT_FOUND("해당 유저를 찾을 수 없습니다.", 404);

    private final String message;
    private final int code;

    UsersErrorCode(String message, int code) {
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
