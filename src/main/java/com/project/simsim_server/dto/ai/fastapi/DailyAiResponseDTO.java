package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class DailyAiResponseDTO {

    private String summary;
    private String reply;
    private List<Integer> emotion;

    public DailyAiResponseDTO(
            String diarySummary,
            String reply,
            List<Integer> emotion) {
        this.summary = diarySummary;
        this.reply = reply;
        this.emotion = emotion;
    }
}
