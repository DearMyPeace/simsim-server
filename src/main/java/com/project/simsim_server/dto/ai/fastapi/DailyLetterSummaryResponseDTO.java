package com.project.simsim_server.dto.ai.fastapi;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DailyLetterSummaryResponseDTO {
    private String result;

    public DailyLetterSummaryResponseDTO(String result) {
        this.result = result;
    }
}
