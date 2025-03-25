package com.project.simsim_server.dto.ai.client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class AIThumsRequestDTO {

    private Long aiId;
    private String thumsStatus;

    @Builder
    public AIThumsRequestDTO(Long aiId, String thumsStatus) {
        this.aiId = aiId;
        this.thumsStatus = thumsStatus;
    }
}
