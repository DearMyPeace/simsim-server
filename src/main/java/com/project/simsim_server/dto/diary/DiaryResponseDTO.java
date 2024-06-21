package com.project.simsim_server.dto.diary;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class DiaryResponseDTO {

    private Long diaryId;
    private Long userId;
    private String content;
    private String deleteYn;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public DiaryResponseDTO(Diary diaryEntity) {
        this.diaryId = diaryEntity.getDiaryId();
        this.userId = diaryEntity.getUserId();
        this.content = diaryEntity.getContent();
        this.deleteYn = diaryEntity.getDiaryDeleteYn();
        this.createdDate = diaryEntity.getCreatedDate();
        this.modifiedDate = diaryEntity.getModifiedDate();
    }
}
