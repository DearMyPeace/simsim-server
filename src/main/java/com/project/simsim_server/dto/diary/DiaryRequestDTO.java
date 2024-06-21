package com.project.simsim_server.dto.diary;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryRequestDTO {

    private Long userId;
    private String content;

    @Builder
    public DiaryRequestDTO(Long userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public Diary toEntity() {
        return Diary.builder()
                .userId(userId)
                .content(content)
                .build();
    }
}
