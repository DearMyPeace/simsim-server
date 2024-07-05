package com.project.simsim_server.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {
    GOOGLE("Google", "구글"),
    APPLE("Apple", "애플");

    private final String key;
    private final String title;
}
