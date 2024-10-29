package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;

import java.util.Map;

@Getter
public class DailyAiKeywordsResponseDTO {
    private String result;

    public DailyAiKeywordsResponseDTO(String result) {
        this.result = result;
    }
}
