package com.project.simsim_server.dto.ai.client;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.ai.MonthlyReport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Getter
@NoArgsConstructor
public class AILetterResponseDTO {
    private Long aiId;
    private Long reportId;
    private LocalDate date;
    private String summary;
    private String content;
    private String replyStatus;

    private String thumbsStatus;

    public AILetterResponseDTO(DailyAiInfo aiEntity) {
        this.aiId = aiEntity.getAiId();
        this.date = aiEntity.getTargetDate();
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        try {
            if (encryptionUtil.isBase64(aiEntity.getDiarySummary())) {
                this.summary = encryptionUtil.decrypt(aiEntity.getDiarySummary());
                this.content = encryptionUtil.decrypt(aiEntity.getReplyContent());
            } else {
                this.summary = aiEntity.getDiarySummary();
                this.content = aiEntity.getReplyContent();
            }
        } catch (Exception e) {
            log.error("---[SimSimInfo] 복호화에 실패했습니다 {}", aiEntity.getAiId());
            throw new RuntimeException("클라이언트 응답 복호화 실패", e);
        }
        this.replyStatus = aiEntity.getReplyStatus();
        this.thumbsStatus = aiEntity.getThumsStatus();
    }

    public AILetterResponseDTO(DailyAiInfo aiEntity, MonthlyReport monthlyReport) {
        this.aiId = aiEntity.getAiId();
        this.reportId = monthlyReport.getMonthReportId();
        this.date = aiEntity.getTargetDate();
        EncryptionUtil encryptionUtil = new EncryptionUtil();
        try {
            if (encryptionUtil.isBase64(aiEntity.getDiarySummary())) {
                this.summary = encryptionUtil.decrypt(aiEntity.getDiarySummary());
                this.content = encryptionUtil.decrypt(aiEntity.getReplyContent());
            } else {
                this.summary = aiEntity.getDiarySummary();
                this.content = aiEntity.getReplyContent();
            }
        } catch (Exception e) {
            log.error("---[SimSimInfo] 복호화에 실패했습니다 {}", aiEntity.getAiId());
            throw new RuntimeException("클라이언트 응답 복호화 실패", e);
        }
        this.replyStatus = aiEntity.getReplyStatus();
    }
}


