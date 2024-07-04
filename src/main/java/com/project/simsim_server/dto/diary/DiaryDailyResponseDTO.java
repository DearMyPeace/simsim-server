package com.project.simsim_server.dto.diary;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DiaryDailyResponseDTO {
    private Boolean sendStatus;
    private List<DiaryResponseDTO> diaries;

    @Builder
    public DiaryDailyResponseDTO(Boolean sendStatus, List<DiaryResponseDTO> diaries) {
        this.sendStatus = sendStatus;
        this.diaries = diaries;
    }
}
