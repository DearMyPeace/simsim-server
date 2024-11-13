//package com.project.simsim_server.service.ai;
//
//import com.project.simsim_server.domain.diary.Diary;
//import com.project.simsim_server.domain.user.Users;
//import com.project.simsim_server.dto.ai.fastapi.DailyAiLetterRequestDTO;
//import com.project.simsim_server.dto.ai.fastapi.DailyLetterSummaryResponseDTO;
//import com.project.simsim_server.repository.diary.DiaryRepository;
//import com.project.simsim_server.repository.user.UsersRepository;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.ManyToOne;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * AI API 테스트
// */
//@Slf4j
//@Transactional
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class AIServiceTest {
//
//    private final Long TEST_USER_ID = 1L;
//
//    @Autowired
//    private UsersRepository usersRepository;
//
//    @Autowired
//    private DiaryRepository diaryRepository;
//
//    @Autowired
//    private AIService aiService;
//
//    @Autowired
//    private TestRestTemplate testTemplate;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    private Users testUser;
//
//    private final String AI_LETTER_URL = "http://52.78.170.42:8000/ai/v1/letter";
//    private final String AI_KEYWORDS_URL = "http://127.0.0.1:8000/ai/v1/keywords";
//    private final String AI_SUMMARY_URL = "http://127.0.0.1:8000/ai/v1/summary";
//
//    @BeforeEach
//    void setUp() {
//        this.testUser = usersRepository.getById(TEST_USER_ID);
//    }
//
//    @Test
//    void AI편지_요청응답() {
//
//        //given
//        LocalDateTime startDateTime = LocalDate.now().minusDays(14).atStartOfDay();
//        LocalDateTime endDateTime = LocalDate.now().atTime(LocalTime.MAX);
//        List<Diary> targetDiaries = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime,
//                endDateTime, TEST_USER_ID);
//
//        assertThat(targetDiaries).isNotEmpty();
//
//        DailyAiLetterRequestDTO requestData = aiService.generateRequestData(testUser, LocalDate.now(), targetDiaries);
//
//        //when
//        ResponseEntity<DailyLetterSummaryResponseDTO> response
//                = testTemplate.postForEntity(AI_LETTER_URL, requestData, DailyLetterSummaryResponseDTO.class);
//
//        //then
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//    }
//}