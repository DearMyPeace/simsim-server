package com.project.simsim_server.dto.ai.fastapi;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiKeywordRequestDTO {
    private List<DiaryContentDTO> diarys;

    @Builder
    public DailyAiKeywordRequestDTO(List<DiaryContentDTO> diarys) {
        this.diarys = diarys;
    }
}
