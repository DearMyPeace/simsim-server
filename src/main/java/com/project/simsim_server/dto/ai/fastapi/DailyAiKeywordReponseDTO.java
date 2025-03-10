package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Getter
@NoArgsConstructor
public class DailyAiKeywordReponseDTO {

    private Map<String, Double> result;

    public DailyAiKeywordReponseDTO(Map<String, Double> result) {
        this.result = result;
    }
}
