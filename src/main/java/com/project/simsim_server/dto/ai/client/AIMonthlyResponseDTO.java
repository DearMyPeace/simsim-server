package com.project.simsim_server.dto.ai.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.MonthlyReport;
import com.project.simsim_server.exception.ai.AIException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@NoArgsConstructor
public class AIMonthlyResponseDTO {
    private Double rate;
    private String keyword;
    private String comment;

    public AIMonthlyResponseDTO(String keyword, Double rate) {
        this.rate = rate;
        this.keyword = keyword;
        this.comment = generateComment(keyword, rate);
    }

    private String generateComment(String keyword, Double rate) {
        int ratePercentage = (int) (rate * 100);
        return keyword + "의 비율은 " + ratePercentage + "% 입니다.";
    }

    public static List<AIMonthlyResponseDTO> convertFromJSONtoObject(String jsonData) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Double> dataMap = objectMapper.readValue(jsonData,
                    new TypeReference<Map<String, Double>>() {});
            return dataMap.entrySet().stream()
                    .map(keywordSet -> new AIMonthlyResponseDTO(keywordSet.getKey(), keywordSet.getValue()))
                    .sorted(Comparator.comparing(AIMonthlyResponseDTO::getRate).reversed())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("---[SimSimError] AI 월간 레포트 JSON 파싱 에러---");
            throw new RuntimeException("AI 키워드 JSON 파싱 실패");
        }
    }
}
