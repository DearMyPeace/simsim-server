//package com.project.simsim_server.repository.ai;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.project.simsim_server.domain.ai.MonthlyReport;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional
////@Rollback(false)
//@SpringBootTest
//class MonthlyReportRepositoryTest {
//
//    private final Long TEST_USER_ID = 1L;
//    private final Long TEST_ID = 1L;
//
//    @Autowired
//    private MonthlyReportRepository monthlyReportRepository;
//
//    @Test
//    void AI응답_저장하기() throws JsonProcessingException {
//        //when
//        String jsonSample = """
//                {
//                    "대피소": 2.6162,
//                    "데이터": 2.0016,
//                    "이용": 1.6581,
//                    "지진": 1.6547,
//                    "정보": 1.5275,
//                    "위치": 1.3227,
//                    "서울시": 1.2679,
//                    "제공": 1.2178,
//                    "인원": 1.2030,
//                    "수": 1.0441,
//                    "파악": 1.0179,
//                    "현황": 1.0020,
//                    "수집": 0.9810,
//                    "분석": 0.9609,
//                    "수용": 0.9328,
//                    "포함": 0.9097,
//                    "가능": 0.9014,
//                    "실시": 0.8676,
//                    "사용": 0.8072,
//                    "주소": 0.7967,
//                    "관리": 0.7964,
//                    "기관": 0.7742,
//                    "서비스": 0.7613,
//                    "예시": 0.7329,
//                    "시각": 0.7292,
//                    "시설": 0.6085,
//                    "종류": 0.6005,
//                    "발생": 0.5885,
//                    "좌표": 0.5851
//                }
//            """;
//        String summarySample = "다사다난한 한달이었네요";
//
//        //given
//        MonthlyReport newData = MonthlyReport.builder()
//                .userId(TEST_USER_ID)
//                .targetDate(LocalDate.now())
//                .keywordsData(jsonSample)
//                .monthlySummary(summarySample)
//                .build();
//        MonthlyReport savedData = monthlyReportRepository.save(newData);
//
//        //then
//        assertThat(savedData.getUserId()).isEqualTo(TEST_USER_ID);
//        assertThat(savedData.getTargetYear()).isEqualTo(LocalDate.now().getYear());
//        assertThat(savedData.getTargetMonth()).isEqualTo(LocalDate.now().getMonthValue());
//        assertThat(savedData.getMonthlySummary()).isEqualTo(summarySample);
//        assertThat(savedData.getKeywordsData()).isEqualTo(jsonSample);
//    }
//
//    @Test
//    void AI응답_업데이트하기() {
//        //when
//        String updateJsonSample = """
//                {
//                    "대피소": 7.6162,
//                    "데이터": 2.0016,
//                    "이용": 1.6581,
//                    "지진": 1.6547,
//                    "정보": 1.5275,
//                    "위치": 1.3227,
//                    "서울시": 1.2679,
//                    "제공": 1.2178,
//                    "인원": 1.2030,
//                    "수": 1.0441,
//                    "파악": 1.0179,
//                    "현황": 1.0020,
//                    "수집": 0.9810,
//                    "분석": 0.9609,
//                    "수용": 0.9328,
//                    "포함": 0.9097,
//                    "가능": 0.9014,
//                    "실시": 0.8676,
//                    "사용": 0.8072,
//                    "주소": 0.7967,
//                    "관리": 0.7964,
//                    "기관": 0.7742,
//                    "서비스": 10.7613,
//                    "예시": 0.7329,
//                    "시각": 0.7292,
//                    "시설": 0.6085,
//                    "종류": 0.6005,
//                    "발생": 0.5885,
//                    "좌표": 0.5851
//                }
//            """;
//        String updateSummarySample = "서비스에 대한 비중이 높아졌어요";
//
//        //given
//        Optional<MonthlyReport> targetData = monthlyReportRepository.findById(TEST_ID);
//        assertThat(targetData.isEmpty()).isFalse();
//        MonthlyReport updatedDate = targetData.get().updateAIResponse(updateJsonSample, updateSummarySample);
//        monthlyReportRepository.save(updatedDate);
//
//        //then
//        assertThat(updatedDate.getUserId()).isEqualTo(TEST_USER_ID);
//        assertThat(updatedDate.getTargetYear()).isEqualTo(LocalDate.now().getYear());
//        assertThat(updatedDate.getTargetMonth()).isEqualTo(LocalDate.now().getMonthValue());
//        assertThat(updatedDate.getMonthlySummary()).isEqualTo(updateSummarySample);
//        assertThat(updatedDate.getKeywordsData()).isEqualTo(updateJsonSample);
//    }
//}