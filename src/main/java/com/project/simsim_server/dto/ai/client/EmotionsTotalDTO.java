package com.project.simsim_server.dto.ai.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmotionsTotalDTO {
    private int analyzePositiveTotal;
    private int analyzeNeutralTotal;
    private int analyzeNegativeTotal;

    public EmotionsTotalDTO(Long analyzePositiveTotal, Long analyzeNeutralTotal, Long analyzeNegativeTotal) {
        this.analyzePositiveTotal = Integer.parseInt(String.valueOf(analyzePositiveTotal));
        this.analyzeNeutralTotal = Integer.parseInt(String.valueOf(analyzeNeutralTotal));
        this.analyzeNegativeTotal = Integer.parseInt(String.valueOf(analyzeNegativeTotal));
    }
}
