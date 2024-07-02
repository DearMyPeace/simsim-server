package com.project.simsim_server.dto.diary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class DiaryCountResponseDTO {

    private LocalDate markedDate;
    private Long diaryCount;

    public DiaryCountResponseDTO(LocalDate targetDate, Long diaryCount) {
        this.markedDate = targetDate;
        this.diaryCount = diaryCount;
    }
}
