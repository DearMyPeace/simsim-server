package com.project.simsim_server.domain.ai;

import com.project.simsim_server.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@ToString
@Getter
@NoArgsConstructor
@Table(name = "daily_ai_response_tbl")
@Entity
public class DailyAiInfo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Long aiId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ai_target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "ai_diary_summary")
    private String diarySummary;

    @Column(name = "ai_reply_content")
    private String replyContent;

    @Column(name = "ai_analyze_emotions")
    private String analyzeEmotions;

    @Column(name = "ai_analyze_factors")
    private String analyzeFactors;

    @Column(name = "ai_reply_status")
    @ColumnDefault("'N'")
    private String replyStatus;


    @Builder
    public DailyAiInfo(Long userId, LocalDate targetDate, String diarySummary,
            String replyContent, String analyzeEmotions, String analyzeFactors,
            String replyStatus) {
        this.userId = userId;
        this.targetDate = targetDate;
        this.diarySummary = diarySummary;
        this.replyContent = replyContent;
        this.analyzeEmotions = analyzeEmotions;
        this.analyzeFactors = analyzeFactors;
        this.replyStatus = "N";
    }

    public DailyAiInfo updateAiResult(String diarySummary, String replyContent) {
        this.diarySummary = diarySummary;
        this.replyContent = replyContent;
        return this;
    }

    public DailyAiInfo updateReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
        return this;
    }
}
