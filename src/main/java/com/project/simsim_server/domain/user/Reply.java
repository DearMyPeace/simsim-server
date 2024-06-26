package com.project.simsim_server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Reply {
    EMPTY("N", "수령할 편지 없음"),
    OCCUPIED("Y", "수령할 편지 있음");

    private final String key;
    private final String title;
}
