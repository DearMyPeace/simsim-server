package com.project.simsim_server.domain.diary;

import com.project.simsim_server.config.encrytion.DatabaseConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@Table(name = "diary_tbl")
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Convert(converter = DatabaseConverter.class)
    @Column(name = "diary_content", columnDefinition = "TEXT", length = 500)
    private String content;

    @Column(name = "diary_list_key", nullable = false)
    private String listKey;

    @Column(name = "diary_delete_yn", nullable = false)
    @ColumnDefault("'N'")
    private String diaryDeleteYn;

    @Column(name = "marked_date", nullable = false)
    private LocalDate markedDate;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Builder
    public Diary(Long userId, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.content = content;
        this.userId = userId;
        this.listKey = userId + "-" + createdDate.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        this.diaryDeleteYn = "N";
        this.markedDate = toLocalDate(createdDate, ZoneId.of("Asia/Seoul"));
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Diary update(String content, LocalDateTime modifiedDate) {
        this.content = content;
        this.modifiedDate = modifiedDate;
        return this;
    }

    public void delete() {
        this.diaryDeleteYn = "Y";
        this.modifiedDate = LocalDateTime.now();
    }

    private LocalDate toLocalDate(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toLocalDate();
    }
}

