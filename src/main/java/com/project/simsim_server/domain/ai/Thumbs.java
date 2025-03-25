package com.project.simsim_server.domain.ai;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Thumbs {

    UP("U", "좋아요"),
    DOWN("D", "싫어요"),
    NONE("N", "선택안함");

    private final String key;
    private final String title;

    public static Thumbs validateString(String thumbsStatus) {
        for (Thumbs status : Thumbs.values()) {
            if (status.getKey().equals(thumbsStatus)) {
                return status;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 값입니다. : " + thumbsStatus);
    }
}
