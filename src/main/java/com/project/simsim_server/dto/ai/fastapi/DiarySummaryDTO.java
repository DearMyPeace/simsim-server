package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiarySummaryDTO {
    private String date;
    private String content;
    private List<Integer> positive;
    private List<Integer> neutral;
    private List<Integer> negative;

    @Builder
    public DiarySummaryDTO(LocalDate date, String content, List<Integer> positive,
        List<Integer> neutral, List<Integer> negative) {
        this.date = date.toString();
        this.content = content;
        this.positive = positive;
        this.neutral = neutral;
        this.negative = negative;
    }
}
