package com.project.simsim_server.service.ai;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.fastapi.*;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.ai.AIErrorCode.AIRESPONE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AIService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;
    private final String AI_LETTER_URL = "http://127.0.0.1:8000/ai/v1/letter";
    private final String AI_KEYWORDS_URL = "http://127.0.0.1:8000/ai/v1/keywords";
    private final String AI_SUMMARY_URL = "http://127.0.0.1:8000/ai/v1/summary";

    /**
     * 1. AI 요청용 DTO 생성
     * @param user
     * @param targetDate
     * @param targetDiaries
     * @return
     */
    public DailyAiLetterRequestDTO generateRequestData(Users user, LocalDate targetDate, List<Diary> targetDiaries) {
        // 페르소나 정보
        String persona = user.getPersona();

        // 14일~2일전 일기 요약 정보
        LocalDate startDate = targetDate.minusDays(14);
        LocalDate endDate = targetDate.minusDays(2);
        List<DailyAiInfo> summaryList = dailyAiInfoRepository.findAllByIdAndTargetDate(user.getUserId(), startDate, endDate);

        List<DiaryContentDTO> diaries = new ArrayList<>();
        for (Diary diary : targetDiaries) {
            diaries.add(DiaryContentDTO.builder()
                    .time(diary.getCreatedDate())
                    .content(diary.getContent())
                    .build());
        }

        // 감정 정보
        List<DiarySummaryDTO> summaries= new ArrayList<>();
        for (DailyAiInfo info : summaryList) {
            summaries.add(DiarySummaryDTO.builder()
                    .date(info.getTargetDate())
                    .content(info.getReplyContent())
                    .build());
        }

        DailyAiLetterRequestDTO requestData = DailyAiLetterRequestDTO.builder()
                .persona(persona)
                .diarys(diaries)
                .summary(summaries)
                .build();

        try {
            String jsonString = objectMapper.writeValueAsString(requestData);
            log.info("---[SimSimSchedule] AI 요청 데이터 = {}", jsonString);
        } catch (IOException e) {
            log.error("---[SimSimSchedule] JSON 직렬화 에러 userId = {}", user.getUserId(), e);
            return null;
        }
        return requestData;
    }


    /**
     * 2. Letter 생성 API 호출
     * @param requestData
     * @return
     */
    public String requestLetter(Users user, DailyAiLetterRequestDTO requestData) {
        ResponseEntity<DailyLetterSummaryResponseDTO> response
                = restTemplate.postForEntity(AI_LETTER_URL, requestData, DailyLetterSummaryResponseDTO.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestLetter AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] requestLetter AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());
        DailyLetterSummaryResponseDTO letter = response.getBody();
        if (letter == null || letter.getResult().isEmpty()) {
            log.error("---[SimSimSchedule] requestLetter AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        return letter.getResult();
    }


    /**
     * 3. 일기 요약 API 호출
     * @param user
     * @return
     */
    public String requestDiarySummary(Users user, DailyAiLetterRequestDTO requestData) {
        ResponseEntity<DailyLetterSummaryResponseDTO> response
                = restTemplate.postForEntity(AI_SUMMARY_URL, requestData, DailyLetterSummaryResponseDTO.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestDiarySummary AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] requestDiarySummary AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());
        DailyLetterSummaryResponseDTO summary = response.getBody();
        if (summary == null || summary.getResult().isEmpty()) {
            log.error("---[SimSimSchedule] requestDiarySummary AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        return summary.getResult();
    }


    /**
     * 4. 키워드 API 호출
     * @param user
     * @return
     */
    public String requestKeywords(Users user, List<Map<String, String>> extractContentOnly) {
        log.info("---[SimSimInfo] requestKeywords 메소드 호출");

        Map<String, Object> requestBody = Map.of("diarys", extractContentOnly);

//        ResponseEntity<DailyAiKeywordsResponseDTO> response
//                = restTemplate.postForEntity(AI_KEYWORDS_URL, requestBody, DailyAiKeywordsResponseDTO.class);
//        if (response.getStatusCode() != HttpStatus.OK) {
//            log.error("---[SimSimSchedule] requestKeywords AI 응답 실패 userId = {}", user.getUserId());
//            return null;
//        }
        try {
            ResponseEntity<DailyAiKeywordsResponseDTO> response =
                    restTemplate.postForEntity(AI_KEYWORDS_URL, requestBody, DailyAiKeywordsResponseDTO.class);
            log.info("---[SimSimSchedule] requestKeywords AI 응답 실패 응답코드 = {}", response.getStatusCode());
            DailyAiKeywordsResponseDTO keywords = response.getBody();

            log.warn("---[SimSimSchedule] requestKeywords AI 응답 내용 = {},  userId = {}", Objects.requireNonNull(response.getBody()).getResult(), user.getUserId());

            if (keywords == null) {
                log.error("---[SimSimSchedule] requestKeywords AI 응답 내용 없음 userId = {}", user.getUserId());
                return null;
            }
            return keywords.getResult();
        } catch (HttpClientErrorException e) {
            log.error("Error response: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
        return null;
    }

    /**
     * AI 응답에 DB 저장 처리
     * @param user
     * @param targetDate
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    @Transactional
    public DailyAiInfo requestToAI(Users user, LocalDate targetDate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("---[SimSimInfo] requestToAI 시작 ---");

        // 예외 처리
        List<Diary> targetDiaries = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime, endDateTime, user.getUserId());
        if (targetDiaries.isEmpty()) {
            log.info("---[SimSimSchedule] 해당 날짜({})에 작성한 일기가 없는 회원 userId = {}", targetDate, user.getUserId());
            return null;
        }

        // AI 요청 정보 생성
        DailyAiLetterRequestDTO requestData = generateRequestData(user, targetDate, targetDiaries);

        log.info("---[SimSimInfo] AI API 시작 ---");
        // AI 요청
        String letter = requestLetter(user, requestData); // AI_LETTER_URL 호출
        log.info("---[SimSimInfo] letter 끝 ---");
        String summary = requestDiarySummary(user, requestData); // AI_SUMMARY_URL 호출
        log.info("---[SimSimInfo] requestDiarySummary 끝 ---");

        List<Map<String, String>> extractContentOnly = targetDiaries.stream()
                .map(diary -> Map.of("content", diary.getContent()))
                .collect(Collectors.toList());

        String keywords = requestKeywords(user, extractContentOnly); //AI_KEYWORDS_URL 호출
        log.info("---[SimSimInfo] requestKeywords 끝 ---");

        if (letter == null || keywords == null || summary == null) {
            throw new AIException(AIRESPONE_NOT_FOUND);
        }

        // 모든 Diary의 isSendAble 상태를 false로 설정.
        diaryRepository.findAllByCreatedAtAndUserId(user.getUserId(), targetDate).forEach(diary -> diary.setIsSendAble(false));

        return dailyAiInfoRepository.save(DailyAiInfo.builder()
                .userId(user.getUserId())
                .targetDate(targetDate)
                .diarySummary(summary)
                .replyContent(letter)
                .replyStatus("N")
                .keywordData(keywords)
                .isFirst(false)
                .build());
    }
}
