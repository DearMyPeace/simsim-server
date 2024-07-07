package com.project.simsim_server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Reply {
    DEFAULT("D", "기본"),
    CHECK("C", "편지 읽음"),
    RECEIVE("R", "편지 안읽음");

    private final String key;
    private final String title;
}
