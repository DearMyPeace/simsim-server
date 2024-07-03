package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.dto.ai.fastapi.DiarySummaryDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 사용자가 편지봉투 클릭 시, 클라이언트로부터 전달 받는 정보
 */
@Setter
@Getter
@NoArgsConstructor
public class AILetterRequestDTO {

    private LocalDate targetDate; // 일기 분석 날짜

    @Builder
    public AILetterRequestDTO(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public DailyAiInfo toEntity() {
        return DailyAiInfo.builder()
                .targetDate(targetDate)
                .build();
    }
}
