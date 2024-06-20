package com.project.simsim_server.dto;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryRequestDTO {

    private Long userPk;
    private String content;

    @Builder
    public DiaryRequestDTO(Long userPk, String content) {
        this.userPk = userPk;
        this.content = content;
    }

    public Diary toEntity() {
        return Diary.builder()
                .userPk(userPk)
                .content(content)
                .build();
    }
}
