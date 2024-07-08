package com.project.simsim_server.dto.ai.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;


@Getter
@NoArgsConstructor
public class WeekSummaryResponseDTO {

    private LocalDate positiveDate;
    private int positiveTotalCnt;
    private String positiveSummaries;
    private LocalDate neutralDate;
    private int neutralTotalCnt;
    private String neutralSummaries;
    private LocalDate negativeDate;
    private int negativeTotalCnt;
    private String negativeSummaries;

    @Builder
    public WeekSummaryResponseDTO(
            LocalDate positiveDate,
            int positiveTotalCnt,
            String positiveSummaries,
            LocalDate neutralDate,
            int neutralTotalCnt,
            String neutralSummaries,
            LocalDate negativeDate,
            int negativeTotalCnt,
            String negativeSummaries

    ) {
        this.positiveDate = positiveDate;
        this.positiveTotalCnt = positiveTotalCnt;
        this.positiveSummaries = positiveSummaries;
        this.neutralDate = neutralDate;
        this.neutralTotalCnt = neutralTotalCnt;
        this.neutralSummaries = neutralSummaries;
        this.negativeDate = negativeDate;
        this.negativeTotalCnt = negativeTotalCnt;
        this.negativeSummaries = negativeSummaries;
    }
}
