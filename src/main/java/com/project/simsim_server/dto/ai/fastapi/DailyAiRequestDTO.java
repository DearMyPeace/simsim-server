package com.project.simsim_server.dto.ai.fastapi;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DailyAiRequestDTO {
    private Long userId;
    private List<String> diary;

    @Builder
    public DailyAiRequestDTO(Long userId, List<String> diary) {
        this.userId = userId;
        this.diary = diary;
    }
}
