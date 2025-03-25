package com.project.simsim_server.domain.ai;

import com.project.simsim_server.config.encrytion.DatabaseConverter;
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

    @Convert(converter = DatabaseConverter.class)
    @Column(name = "ai_diary_summary")
    private String diarySummary;

    @Convert(converter = DatabaseConverter.class)
    @Column(name = "ai_reply_content")
    private String replyContent;

    @Column(name = "ai_happy_cnt", nullable = false)
    @ColumnDefault("0")
    private int happyCnt;

    @Column(name = "ai_appreciation_cnt", nullable = false)
    @ColumnDefault("0")
    private int appreciationCnt;

    @Column(name = "ai_love_cnt", nullable = false)
    @ColumnDefault("0")
    private int loveCnt;

    @Column(name = "ai_analyze_positive")
    private String analyzePositive;

    @Column(name = "ai_analyze_positive_total", nullable = false)
    @ColumnDefault("0")
    private int analyzePositiveTotal;

    @Column(name = "ai_tranquility_cnt", nullable = false)
    @ColumnDefault("0")
    private int tranquilityCnt;

    @Column(name = "ai_curiosity_cnt", nullable = false)
    @ColumnDefault("0")
    private int curiosityCnt;

    @Column(name = "ai_surprise_cnt", nullable = false)
    @ColumnDefault("0")
    private int surpriseCnt;

    @Column(name = "ai_analyze_neutral")
    private String analyzeNeutral;

    @Column(name = "ai_analyze_neutral_total", nullable = false)
    @ColumnDefault("0")
    private int analyzeNeutralTotal;

    @Column(name = "ai_sad_cnt", nullable = false)
    @ColumnDefault("0")
    private int sadCnt;

    @Column(name = "ai_angry_cnt", nullable = false)
    @ColumnDefault("0")
    private int angryCnt;

    @Column(name = "ai_fear_cnt", nullable = false)
    @ColumnDefault("0")
    private int fearCnt;

    @Column(name = "ai_analyze_negative")
    private String analyzeNegative;

    @Column(name = "ai_analyze_negative_total", nullable = false)
    @ColumnDefault("0")
    private int analyzeNegativeTotal;

    @Column(name = "ai_analyze_factors")
    private String analyzeFactors;

    @Column(name = "ai_reply_status", nullable = false)
    @ColumnDefault("'N'")
    private String replyStatus;

    @Column(name="ai_is_first", nullable = false)
    @ColumnDefault("false")
    private boolean isFirst;

    @Column(name="ai_thumbs_status", nullable = false)
    @ColumnDefault("'N'")
    private String thumbsStatus;

    @Builder
    public DailyAiInfo(Long userId, LocalDate targetDate, String diarySummary,
            String replyContent, String analyzePositive, String analyzeNeutral, String analyzeNegative,
            int analyzePositiveTotal, int analyzeNeutralTotal, int analyzeNegativeTotal,
            String analyzeFactors, String replyStatus, boolean isFirst,
            int happyCnt, int appreciationCnt, int loveCnt,
            int tranquilityCnt, int curiosityCnt, int surpriseCnt,
            int sadCnt, int angryCnt, int fearCnt, String thumbsStatus) {
        this.userId = userId;
        this.targetDate = targetDate;
        this.diarySummary = diarySummary;
        this.replyContent = replyContent;
        this.analyzePositive = analyzePositive;
        this.analyzePositiveTotal = analyzePositiveTotal;
        this.analyzeNeutral = analyzeNeutral;
        this.analyzeNeutralTotal = analyzeNeutralTotal;
        this.analyzeNegative = analyzeNegative;
        this.analyzeNegativeTotal = analyzeNegativeTotal;
        this.analyzeFactors = analyzeFactors;
        this.replyStatus = replyStatus;
        this.isFirst = isFirst;
        this.happyCnt = happyCnt;
        this.appreciationCnt = appreciationCnt;
        this.loveCnt = loveCnt;
        this.tranquilityCnt = tranquilityCnt;
        this.curiosityCnt = curiosityCnt;
        this.surpriseCnt = surpriseCnt;
        this.sadCnt = sadCnt;
        this.angryCnt = angryCnt;
        this.fearCnt = fearCnt;
        this.thumbsStatus = thumbsStatus;
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

    public DailyAiInfo updateThumbsStatus(String thumbsStatus) {
        this.thumbsStatus = thumbsStatus;
        return this;
    }
}
