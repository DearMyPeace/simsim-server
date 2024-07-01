package com.project.simsim_server.exception;

public enum AuthErrorCode implements ErrorType {

    JWT_NOT_VALID("JWT가 유효하지 않습니다.", 403),
    ACCESS_TOKEN_NOT_EXIST("액세스 토큰이 존재하지 않습니다.", 403),
    ACCESS_TOKEN_EXPIRED("액세스 토큰의 기한이 만료되었습니다.", 401),
    REFRESH_TOKEN_NOT_EXIST("리프레시 토큰이 존재하지 않습니다.", 403),
    REFRESH_TOKEN_EXPIRED("리프레시 토큰의 기한이 만료되었습니다.", 401),
    REFRESH_TOKEN_NOT_SAME("접근할 수 없습니다.", 403),
    LOGIN_FAILED("로그인 실패", 500),
    INVALID_JWT_SIGNATURE("서명이 유효하지 않습니다.", 403);

    private final String message;
    private final int code;

    AuthErrorCode(String message, int code) {
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
