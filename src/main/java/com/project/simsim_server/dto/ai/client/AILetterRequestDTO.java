package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 사용자가 편지봉투 클릭 시, 클라이언트로부터 전달 받는 정보
 */
@Getter
@NoArgsConstructor
public class AILetterRequestDTO {
    private Long userId;
    private LocalDate targetDate;

    @Builder
    public AILetterRequestDTO(Long userId, LocalDate targetDate) {
        this.userId = userId;
        this.targetDate = targetDate;
    }

    public DailyAiInfo toEntity() {
        return DailyAiInfo.builder()
                .userId(userId)
                .targetDate(targetDate)
                .build();
    }
}
