package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiResponseDTO {

    private String diarySummary;
    private String reply;
    private List<Integer> emotion;

    public DailyAiResponseDTO(
            String diarySummary,
            String reply,
            List<Integer> emotion) {
        this.diarySummary = diarySummary;
        this.reply = reply;
        this.emotion = emotion;
    }
}
