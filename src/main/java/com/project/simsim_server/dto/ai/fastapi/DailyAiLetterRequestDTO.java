package com.project.simsim_server.dto.ai.fastapi;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiLetterRequestDTO {
    private List<DiaryContentDTO> diarys;
    private List<DiaryContentDTO> monthlyDiaries;
    private String persona;
    private List<DiarySummaryDTO> summary;

    @Builder
    public DailyAiLetterRequestDTO(List<DiaryContentDTO> diarys, List<DiaryContentDTO> monthlyDiaries, String persona, List<DiarySummaryDTO> summary) {
        this.diarys = diarys;
        this.monthlyDiaries = monthlyDiaries;
        this.persona = persona;
        this.summary = summary;
    }
}
