package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiarySummaryDTO {
    private String date;
    private String content;

    @Builder
    public DiarySummaryDTO(LocalDate date, String content) {
        this.date = date.toString();
        this.content = content;
    }
}
