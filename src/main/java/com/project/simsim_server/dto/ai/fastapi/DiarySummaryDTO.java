package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiarySummaryDTO {

    private LocalDate date;
    private String content;
    private List<String> emotion;

    @Builder
    public DiarySummaryDTO(LocalDate date, String content, List<String> emotion) {
        this.date = date;
        this.content = content;
        this.emotion = emotion;
    }
}
