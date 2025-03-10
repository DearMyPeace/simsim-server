package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class DiaryContentDTO {
    private String time;
    private String content;

    @Builder
    public DiaryContentDTO(LocalDateTime time, String content) {
        this.time = time.toString();
        this.content = content;
    }
}
