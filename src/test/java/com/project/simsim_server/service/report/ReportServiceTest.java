package com.project.simsim_server.service.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.MonthlyReport;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.ai.client.AIMonthlyResponseDTO;
import com.project.simsim_server.dto.ai.client.WeekSummaryResponseDTO;
import com.project.simsim_server.exception.ai.AIErrorCode;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.MonthlyReportRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    private final Long TEST_USER_ID = 1L;

    @Test
    void 월간레포트_확인() {
        // given
        String targetDate = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyyMM"));

        // when
        List<AIMonthlyResponseDTO> reponseList = reportService.findByuserIdAndTargetDate(TEST_USER_ID, targetDate);

        // then
        assertThat(reponseList).isNotNull();
        assertThat(reponseList.get(0).getKeyword()).isEqualTo("카공");
        assertThat(reponseList.get(0).getRate()).isEqualTo(3.0867);
        assertThat(reponseList.get(0).getComment()).isEqualTo("카공의 비율은 3% 입니다.");
    }

    @Test
    void 월간레포트_미래날짜_확인() {
        // given
        String targetDate = YearMonth.of(2026, 2).format(DateTimeFormatter.ofPattern("yyyyMM"));

        // when & then
        assertThatThrownBy(() -> reportService.findByuserIdAndTargetDate(TEST_USER_ID, targetDate))
                .isInstanceOf(AIException.class)
                .extracting(ex -> (AIException) ex)
                .extracting(AIException::getErrorType)
                .isInstanceOf(AIErrorCode.class)
                .satisfies(errorType -> {
                    AIErrorCode aiErrorCode = (AIErrorCode) errorType;
                    assertThat(aiErrorCode.getMessage()).isEqualTo(AIErrorCode.AI_NOT_INVALID_DATE.getMessage());
                    assertThat(aiErrorCode.getCode()).isEqualTo(AIErrorCode.AI_NOT_INVALID_DATE.getCode());
                });
    }

    @Test
    void 월간레포트_기록이없는월_확인() {
        // given
        String targetDate = YearMonth.of(2025, 1).format(DateTimeFormatter.ofPattern("yyyyMM"));

        // when & then
        assertThatThrownBy(() -> reportService.findByuserIdAndTargetDate(TEST_USER_ID, targetDate))
                .isInstanceOf(AIException.class)
                .extracting(ex -> (AIException) ex) // AIException으로 추출
                .extracting(AIException::getErrorType) // getErrorType() 호출로 AIErrorCode 추출
                .isInstanceOf(AIErrorCode.class) // AIErrorCode 타입인지 확인
                .satisfies(errorType -> {
                    AIErrorCode aiErrorCode = (AIErrorCode) errorType;
                    assertThat(aiErrorCode.getMessage()).isEqualTo(AIErrorCode.REPORT_NOT_FOUND.getMessage());
                    assertThat(aiErrorCode.getCode()).isEqualTo(AIErrorCode.REPORT_NOT_FOUND.getCode());
                });
    }
}