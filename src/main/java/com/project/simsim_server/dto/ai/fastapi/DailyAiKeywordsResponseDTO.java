package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;

@Getter
public class DailyAiKeywordsResponseDTO {
    private String data;  // JSON 데이터를 String으로 관리

    public DailyAiKeywordsResponseDTO(String data) {
        this.data = data;
    }
}
