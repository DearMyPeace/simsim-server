package com.project.simsim_server.dto;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DiaryResponseDTO {

    private Long diaryPk;
    private Long userPk;
    private String content;
    private String deleteYn;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public DiaryResponseDTO(Diary diaryEntity) {
        this.diaryPk = diaryEntity.getDiaryPk();
        this.userPk = diaryEntity.getUserPk();
        this.content = diaryEntity.getContent();
        this.deleteYn = diaryEntity.getDiaryDeleteYn();
        this.createdDate = diaryEntity.getCreatedDate();
        this.modifiedDate = diaryEntity.getModifiedDate();
    }
}
