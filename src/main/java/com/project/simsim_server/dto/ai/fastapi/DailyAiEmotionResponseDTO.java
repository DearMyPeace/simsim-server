package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiEmotionResponseDTO {
    List<Integer> positive;
    int positive_total;
    List<Integer> neutral;
    int neutral_total;
    List<Integer> negative;
    int negative_total;

    public DailyAiEmotionResponseDTO(
            List<Integer> positive,
            int positive_total,
            List<Integer> neutral,
            int neutral_total,
            List<Integer> negative,
            int negative_total
    ) {
        this.positive = positive;
        this.positive_total = positive_total;
        this.neutral = neutral;
        this.neutral_total = neutral_total;
        this.negative = negative;
        this.negative_total = negative_total;
    }
}
