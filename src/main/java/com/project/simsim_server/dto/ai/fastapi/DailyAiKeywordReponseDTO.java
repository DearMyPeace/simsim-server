package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class DailyAiKeywordReponseDTO {

    private Map<String, Double> result;

    public DailyAiKeywordReponseDTO(Map<String, Double> result) {
        this.result = result;
    }
}
