package com.project.simsim_server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Grade {
    GENERAL("GENERAL", "일반"),
    PREMIUM("PREMIUM", "프리미엄");

    private final String key;
    private final String title;
}