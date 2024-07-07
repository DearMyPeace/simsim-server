package com.project.simsim_server.dto.ai.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WeekReportResponseDTO {
    List<Integer> positive;
    int positive_total;
    List<Integer> neutral;
    int neutral_total;
    List<Integer> negative;
    int negative_total;
}

