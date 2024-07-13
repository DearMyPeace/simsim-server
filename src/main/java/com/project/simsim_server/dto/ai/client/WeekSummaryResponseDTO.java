package com.project.simsim_server.dto.ai.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
public class WeekSummaryResponseDTO {

    @Setter
    private int diaryCnt;
    private LocalDate positiveDate;
    private int positiveTotalCnt;
    private String positiveSummary;
    private LocalDate neutralDate;
    private int neutralTotalCnt;
    private String neutralSummary;
    private LocalDate negativeDate;
    private int negativeTotalCnt;
    private String negativeSummary;

    @Builder
    public WeekSummaryResponseDTO(
            int diaryCnt,
            LocalDate positiveDate,
            int positiveTotalCnt,
            String positiveSummary,
            LocalDate neutralDate,
            int neutralTotalCnt,
            String neutralSummary,
            LocalDate negativeDate,
            int negativeTotalCnt,
            String negativeSummary

    ) {
        this.diaryCnt = diaryCnt;
        this.positiveDate = positiveDate;
        this.positiveTotalCnt = positiveTotalCnt;
        this.positiveSummary = positiveSummary;
        this.neutralDate = neutralDate;
        this.neutralTotalCnt = neutralTotalCnt;
        this.neutralSummary = neutralSummary;
        this.negativeDate = negativeDate;
        this.negativeTotalCnt = negativeTotalCnt;
        this.negativeSummary = negativeSummary;
    }
}
