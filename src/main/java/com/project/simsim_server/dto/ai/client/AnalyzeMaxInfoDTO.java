package com.project.simsim_server.dto.ai.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
public class AnalyzeMaxInfoDTO {
    private Long aiId;
    private LocalDate maxDate;
    private int emotionTotal;

    public AnalyzeMaxInfoDTO(Long aiId, LocalDate maxDate, int emotionTotal) {
        this.aiId = aiId;
        this.maxDate = maxDate;
        this.emotionTotal = emotionTotal;
    }
}
