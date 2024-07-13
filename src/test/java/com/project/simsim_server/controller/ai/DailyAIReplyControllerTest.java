//package com.project.simsim_server.controller.ai;
//
//import com.project.simsim_server.domain.ai.DailyAiInfo;
//import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
//import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
//import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//
//
///**
// * 등록 / 수정 테스트는 Postman과 DBeaver로 확인
// * (PATCH Request를 보내는 방법 확인 중)
// */
//@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
//class DailyAIReplyControllerTest {
//
//    @LocalServerPort
//    private int port;
//
//    @Autowired
//    private TestRestTemplate testTemplate;
//
//    @Autowired
//    private DailyAiInfoRepository dailyRepository;
//
//    @AfterEach
//    void cleanUp() {
//        dailyRepository.deleteAll();
//    }
//
//
//    private ResponseEntity<AILetterResponseDTO> makeTestSample(LocalDate targetDate) {
//        String url = "http://localhost:" + port + "/api/v1/aiLetters/save";
//
//        //when
//        AILetterRequestDTO sample = new AILetterRequestDTO(targetDate);
//        return testTemplate.postForEntity(url, sample, AILetterResponseDTO.class);
//    }
//
//    @Test
//    void AI_등록요청() {
//        //given
//        Long userId = 1L;
//        LocalDate targetDate = LocalDate.of(2020, 1, 1);
//        ResponseEntity<AILetterResponseDTO> result = makeTestSample(targetDate);
//
//        //then
//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(result.getBody()).isNotNull();
//
//        List<DailyAiInfo> aiInfos = dailyRepository.findAll();
//        assertThat(aiInfos.getFirst().getUserId()).isEqualTo(userId);
//        assertThat(aiInfos.getFirst().getTargetDate()).isEqualTo("2020-01-01");
//    }
//
//
//    @Test
//    void AI편지_조회요청_오프셋없음() {
//        //given
//        LocalDate targetDate = LocalDate.of(2024, 1, 1);
//        makeTestSample(targetDate);
//
//        targetDate = LocalDate.of(2020, 7, 20);
//        makeTestSample(targetDate);
//
//    }
//}