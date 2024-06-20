package com.project.simsim_server.domain.diary;

import com.project.simsim_server.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
@Entity
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_pk")
    private Long diaryPk;

    @Column(name = "user_pk", nullable = false)
    private Long userPk;

    @Column(name = "diary_content", columnDefinition = "TEXT", length = 500)
    private String content;

    @Column(name = "diary_list_key", nullable = false)
    private String listKey;

    @Column(name = "diary_delete_yn", nullable = false)
    @ColumnDefault("'N'")
    private String diaryDeleteYn;

    @Builder
    public Diary(Long userPk, String content) {
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        this.content = content;
        this.userPk = userPk;
        this.listKey = userPk + "-" + localDateTimeNow.format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        this.diaryDeleteYn = "N";
    }

    public Diary update(String content) {
        this.content = content;
        return this;
    }

    public void delete() {
        this.diaryDeleteYn = "Y";
    }
}
