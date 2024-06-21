package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Getter
@NoArgsConstructor
public class AILetterResponseDTO {
    private Long id;
    private LocalDate date;
    private String summary;
    private String content;

    public AILetterResponseDTO(DailyAiInfo aiEntity) {
        this.id = aiEntity.getAiId();
        this.date = aiEntity.getTargetDate();
        this.summary = aiEntity.getDiarySummary();
        this.content = aiEntity.getReplyContent();
    }
}


