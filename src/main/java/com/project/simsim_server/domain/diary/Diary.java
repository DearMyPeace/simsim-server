package com.project.simsim_server.domain.diary;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
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

    @Column(name = "diary_content", columnDefinition = "TEXT", length = 500)
    private String content;

    @Column(name = "diary_list_key", nullable = false)
    private String listKey;

    @Column(name = "diary_delete_yn", nullable = false)
    @ColumnDefault("'N'")
    private String diaryDeleteYn;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

    @Builder
    public Diary(Long userId, String content, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.content = content;
        this.userId = userId;
        this.listKey = userId + "-" + createdDate.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        this.diaryDeleteYn = "N";
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
    }
}

