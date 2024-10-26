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
        this.createdDate = convertToUTC(diaryEntity.getCreatedDate());
//        this.modifiedDate = convertToUTC(diaryEntity.getModifiedDate());

        log.info("------[SimSimInfo] Response 일기 생성 시각 : {} ----------------", this.createdDate);
        log.info("------[SimSimInfo] Response 일기 수정 시각 : {} ----------------", this.modifiedDate);
    }

    private String convertToUTC(LocalDateTime localDateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));
        return zonedDateTime.format(DateTimeFormatter.ISO_INSTANT);
    }
}
