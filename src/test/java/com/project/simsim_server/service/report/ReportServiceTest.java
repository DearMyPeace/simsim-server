//package com.project.simsim_server.service.report;
//
//import com.project.simsim_server.domain.diary.Diary;
//import com.project.simsim_server.dto.ai.client.WeekSummaryResponseDTO;
//import com.project.simsim_server.repository.diary.DiaryRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//class ReportServiceTest {
//
//    @Autowired
//    private ReportService reportService;
//
//    @Autowired
//    private DiaryRepository diaryRepository;
//
//    @Test
//    void N번요약결과_확인() {
//        // given
//        Long userId = 1L;
//        LocalDate today = LocalDate.now();
//        LocalDateTime startDate = LocalDate.of(2024, 1, 1).atStartOfDay();
//        LocalDateTime endDate = today.atTime(LocalTime.now());
//
//        // when
//        WeekSummaryResponseDTO results = reportService.weekReportSummary(userId, today);
//        List<Diary> diaries = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDate, endDate, userId);
//
//        // then
//        assertThat(results).isNotNull();
//        assertThat(diaries).isNotNull();
//        assertThat(diaries.size()).isEqualTo(results.getDiaryCnt());
//    }
//}