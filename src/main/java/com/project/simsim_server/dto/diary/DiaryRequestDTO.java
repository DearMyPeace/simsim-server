package com.project.simsim_server.dto.diary;

import com.project.simsim_server.domain.diary.Diary;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class DiaryRequestDTO {

    private Long userId;
    private String content;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createdDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime modifiedDate;

    @Builder
    public DiaryRequestDTO(Long userId, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.userId = userId;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Diary toEntity() {
        return Diary.builder()
                .userId(userId)
                .content(content)
                .createdDate(ZonedDateTime.of(createdDate, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime())
                .modifiedDate(ZonedDateTime.of(modifiedDate, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}
