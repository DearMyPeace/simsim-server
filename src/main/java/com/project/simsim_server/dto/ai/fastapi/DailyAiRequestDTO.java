package com.project.simsim_server.dto.ai.fastapi;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiRequestDTO {
    private Long userId;
    private LocalDate targetDate;
    private List<String> diary;
    private String persona;
    private List<DiarySummaryDTO> summary;

    @Builder
    public DailyAiRequestDTO(Long userId, List<String> diary, LocalDate targetDate, String persona, List<DiarySummaryDTO> summary) {
        this.userId = userId;
        this.targetDate = targetDate;
        this.diary = diary;
        this.persona = persona;
        this.summary = summary;
    }
}
