package com.project.simsim_server.domain.ai;


import com.project.simsim_server.config.encrytion.DatabaseConverter;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
@Table(name = "monthly_report_tbl")
@Entity
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "month_report_id")
    private Long monthReportId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "mr_target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "mr_target_year", nullable = false)
    private int targetYear;

    @Column(name = "mr_target_month", nullable = false)
    private int targetMonth;

//    @Convert(converter = DatabaseConverter.class)
    @Column(name = "mr_summary")
    private String monthlySummary;

    @Column(name = "mr_keywords_data", columnDefinition = "JSON")
    private String keywordsData;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;


    @Builder
    public MonthlyReport(Long userId, LocalDate targetDate, String keywordsData, String monthlySummary) {
        this.userId = userId;
        this.targetDate = targetDate;
        this.targetYear = targetDate.getYear();
        this.targetMonth = targetDate.getMonthValue();
        this.keywordsData = keywordsData != null ? keywordsData : "{}";
        this.monthlySummary = monthlySummary;
        this.createdDate = LocalDateTime.now();
        this.modifiedDate = LocalDateTime.now();
    }

    public MonthlyReport updateAIResponse(String keywordsData, String monthlySummary) {
        this.keywordsData = keywordsData;
        this.monthlySummary = monthlySummary;
        this.modifiedDate = LocalDateTime.now();

        return this;
    }
}
