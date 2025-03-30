package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.user.Persona;
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

    private LocalDate targetDate;
    private String personaCode;

    @Builder
    public AILetterRequestDTO(LocalDate targetDate, String personaCode) {
        this.targetDate = targetDate;
        this.personaCode = personaCode;
    }

    public DailyAiInfo toEntity() {
        return DailyAiInfo.builder()
                .targetDate(targetDate)
                .build();
    }
}
