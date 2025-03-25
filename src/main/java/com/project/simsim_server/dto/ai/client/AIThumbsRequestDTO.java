package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
public class AIThumbsRequestDTO {

    private Long aiId;
    private String thumbsStatus;

    @Builder
    public AIThumbsRequestDTO(Long aiId, String thumbsStatus) {
        this.aiId = aiId;
        this.thumbsStatus = thumbsStatus;
    }
}
