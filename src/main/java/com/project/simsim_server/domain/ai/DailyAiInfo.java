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
import java.util.Map;

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

    @Column(name = "ai_reply_status", nullable = false)
    @ColumnDefault("'N'")
    private String replyStatus;

    @Column(name="ai_is_first", nullable = false)
    @ColumnDefault("false")
    private boolean isFirst;

    @Convert(converter = MapToJsonConverter.class)
    @Column(name = "ai_keyword_data", columnDefinition = "json")
    private Map<String, Double> keywordData;

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

    @Builder
    public DailyAiInfo(Long userId, LocalDate targetDate, String diarySummary,
            String replyContent, String replyStatus, boolean isFirst, Map<String, Double> keywordData) {
        this.userId = userId;
        this.targetDate = targetDate;
        this.diarySummary = diarySummary;
        this.replyContent = replyContent;
        this.replyStatus = replyStatus;
        this.isFirst = isFirst;
        this.keywordData = keywordData;

        this.happyCnt = 0;
        this.appreciationCnt = 0;
        this.loveCnt = 0;
        this.analyzePositive = "";
        this.analyzePositiveTotal = 0;
        this.tranquilityCnt = 0;
        this.curiosityCnt = 0;
        this.surpriseCnt = 0;
        this.analyzeNeutral = "";
        this.analyzeNeutralTotal = 0;
        this.sadCnt = 0;
        this.angryCnt = 0;
        this.fearCnt = 0;
        this.analyzeNegative = "";
        this.analyzeNegativeTotal = 0;
    }

    public DailyAiInfo updateAiResult(String diarySummary, String replyContent, Map<String, Double> keywordData) {
        this.diarySummary = diarySummary;
        this.replyContent = replyContent;
        this.keywordData = keywordData;
        return this;
    }

    public DailyAiInfo updateReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
        return this;
    }
}
