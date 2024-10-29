package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;

import java.util.Map;

@Getter
public class DailyAiKeywordsResponseDTO {
    private Map<String, Double> result;

    public DailyAiKeywordsResponseDTO(Map<String, Double> result) {
        this.result = result;
    }
}
