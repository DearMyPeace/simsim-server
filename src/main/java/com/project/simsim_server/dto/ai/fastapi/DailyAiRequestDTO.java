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
    private String targetDate;
    private List<String> diarys;
    private String persona;
    private List<DiarySummaryDTO> summary;

    @Builder
    public DailyAiRequestDTO(List<String> diarys, LocalDate targetDate, String persona, List<DiarySummaryDTO> summary) {
        this.targetDate = targetDate.toString();
        this.diarys = diarys;
        this.persona = persona;
        this.summary = summary;
    }
}
