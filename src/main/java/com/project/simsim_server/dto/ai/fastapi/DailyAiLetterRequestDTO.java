package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiLetterRequestDTO {
    private List<DiaryContentDTO> diarys;
    private String persona;
    private List<DiarySummaryDTO> summary;

    @Builder
    public DailyAiLetterRequestDTO(List<DiaryContentDTO> diarys, String persona, List<DiarySummaryDTO> summary) {
        this.diarys = diarys;
        this.persona = persona;
        this.summary = summary;
    }
}
