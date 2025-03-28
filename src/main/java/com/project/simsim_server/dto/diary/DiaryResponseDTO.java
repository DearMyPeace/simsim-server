package com.project.simsim_server.dto.diary;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.diary.Diary;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Getter
@NoArgsConstructor
public class DiaryResponseDTO {

    private Long diaryId;
    private Long userId;
    private String content;
    private String deleteYn;
    private LocalDate markedDate;
    private String createdDate;
    private String modifiedDate;

    public DiaryResponseDTO(Diary diaryEntity) {
        this.diaryId = diaryEntity.getDiaryId();
        this.userId = diaryEntity.getUserId();
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        try {
            if (encryptionUtil.isBase64(diaryEntity.getContent())) {
                this.content = encryptionUtil.decrypt(diaryEntity.getContent());
            } else {
                this.content = diaryEntity.getContent();
            }
        } catch (Exception e) {
            log.error("---[SimSimInfo] 복호화에 실패했습니다 {}", diaryEntity.getDiaryId());
            throw new RuntimeException("클라이언트 응답 복호화 실패", e);
        }
        this.deleteYn = diaryEntity.getDiaryDeleteYn();
        this.markedDate = diaryEntity.getMarkedDate();
        this.createdDate = convertToKST(diaryEntity.getCreatedDate());
        this.modifiedDate = convertToKST(diaryEntity.getModifiedDate());
    }

    private String convertToKST(LocalDateTime utcDateTime) {
        ZonedDateTime kstDateTime = ZonedDateTime.of(utcDateTime, ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.of("Asia/Seoul"));
        return kstDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }
}
