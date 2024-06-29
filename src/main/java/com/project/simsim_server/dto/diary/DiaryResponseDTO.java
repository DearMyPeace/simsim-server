package com.project.simsim_server.dto.diary;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class DiaryResponseDTO {

    private Long diaryId;
    private Long userId;
    private String content;
    private String deleteYn;
    private String createdDate;
    private String modifiedDate;

    public DiaryResponseDTO(Diary diaryEntity) {
        this.diaryId = diaryEntity.getDiaryId();
        this.userId = diaryEntity.getUserId();
        this.content = diaryEntity.getContent();
        this.deleteYn = diaryEntity.getDiaryDeleteYn();
        this.createdDate = convertToUTC(diaryEntity.getCreatedDate());
        this.modifiedDate = convertToUTC(diaryEntity.getModifiedDate());
    }

    private String convertToUTC(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        return zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
    }
}
