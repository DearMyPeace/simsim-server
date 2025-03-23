package com.project.simsim_server.service.ai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.ai.MonthlyReport;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.*;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.ai.MonthlyReportRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.ai.AIErrorCode.AIRESPONE_NOT_FOUND;
import static com.project.simsim_server.exception.ai.AIErrorCode.AI_MAIL_FAIL;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AIService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final String AI_LETTER_URL = "http://127.0.0.1:8000/ai/v1/letter";
    private final String AI_SUMMARY_URL = "http://127.0.0.1:8000/ai/v1/summary";
    private final String AI_KEYWORDS_URL = "http://127.0.0.1:8000/ai/v1/keywords";


    /**
     * 1. AI 요청용 DTO 생성
     */
    public DailyAiLetterRequestDTO generateRequestData(Users user, LocalDate targetDate, List<Diary> targetDiaries) {
        // 페르소나 정보
        String persona = user.getPersona();
        log.warn("----[분석할 페르소나] {} : {}", user.getEmail(), user.getPersona());

        // 요청 일자 하루치의 기록(AI 편지 생성용)
        List<DiaryContentDTO> diaries = new ArrayList<>();
        for (Diary diary : targetDiaries) {
            diaries.add(DiaryContentDTO.builder()
                    .time(diary.getCreatedDate())
                    .content(diary.getContent())
                    .build());
        }

        // 2주간 AI 응답 정보(14일 ~ 2일전)
        LocalDate startDate = targetDate.minusDays(14);
        LocalDate endDate = targetDate.minusDays(2);
        List<DailyAiInfo> summaryList = dailyAiInfoRepository.findAllByIdAndTargetDate(user.getUserId(), startDate, endDate);

        List<DiarySummaryDTO> summaries= new ArrayList<>();
        for (DailyAiInfo info : summaryList) {
            summaries.add(DiarySummaryDTO.builder()
                    .date(info.getTargetDate())
                    .content(info.getReplyContent())
                    .positive(new ArrayList<>(Arrays.asList(0, 0, 0)))
                    .neutral(new ArrayList<>(Arrays.asList(0, 0, 0)))
                    .negative(new ArrayList<>(Arrays.asList(0, 0, 0)))
                    .build());
        }

        // 월별 기록 정보(키워드 정보 생성용)
        LocalDateTime startDateTime = targetDate.withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = targetDate.atTime(LocalTime.MAX);
        List<Diary> monthlyDiariesInfo = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime, endDateTime, user.getUserId());
        List<DiaryContentDTO> monthlyDiaries = new ArrayList<>();
        for(Diary diary : monthlyDiariesInfo) {
            monthlyDiaries.add(DiaryContentDTO.builder()
                    .time(diary.getCreatedDate())
                    .content(diary.getContent())
                    .build());
        }

        DailyAiLetterRequestDTO requestData = DailyAiLetterRequestDTO.builder()
                .targetDate(targetDate)
                .persona(persona)
                .diarys(diaries) // 요청일 하루의 일기 - AI 편지 생성용
                .monthlyDiaries(monthlyDiaries) // 요청일 한달간의 일기 - 키워드 생성용
                .summary(summaries) // 요청일 이전 2주간의 AI 응답
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
     * 4. 월간 키워드 API 호출
     */
    public String requestKeywords(Users user, DailyAiLetterRequestDTO requestData) throws JsonProcessingException {

        if (requestData.getMonthlyDiaries() == null || requestData.getMonthlyDiaries().isEmpty()) {
            log.error("---[SimSimError] requestKeywords 요청 데이터가 없습니다. userId = {}", user.getUserId());
            return null;
        }

        DailyAiKeywordRequestDTO keywordRequestDTO = DailyAiKeywordRequestDTO.builder()
                .diarys(requestData.getMonthlyDiaries())
                .build();

        ResponseEntity<String> response
                = restTemplate.postForEntity(AI_KEYWORDS_URL, keywordRequestDTO, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestKeywords AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        String keywords = response.getBody();
        if (keywords == null) {
            log.error("---[SimSimSchedule] requestKeywords AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        log.warn("---[SimSimSchedule] requestKeywords AI 응답 내용 = {},  userId = {}", keywords, user.getUserId());

        return keywords;
    }


    /**
     * AI 응답에 DB 저장 처리
     */
    @Transactional
    public AILetterResponseDTO requestToAI(Users user, LocalDate targetDate, List<Diary> targetDiaries) throws JsonProcessingException {

        // AI 요청 정보 생성
        DailyAiLetterRequestDTO requestData = generateRequestData(user, targetDate, targetDiaries);

        // AI 요청
        String letter = requestLetter(user, requestData);
        String summary = requestDiarySummary(user, requestData);
        String keywords = requestKeywords(user, requestData);
        if (letter == null || keywords == null || summary == null) {
            throw new AIException(AIRESPONE_NOT_FOUND);
        }

        // 모든 Diary의 isSendAble 상태를 false로 설정
        List<Diary> diaries = diaryRepository.findAllByCreatedAtAndUserId(user.getUserId(), targetDate);
        diaries.forEach(diary -> diary.setIsSendAble(false));
        diaryRepository.saveAll(diaries);
        DailyAiInfo aiData = dailyAiInfoRepository.save(DailyAiInfo.builder()
                .userId(user.getUserId())
                .targetDate(targetDate)
                .diarySummary(summary)
                .replyContent(letter)
                .replyStatus("N")
                .isFirst(false)
                .build());

        int targetYear = targetDate.getYear();
        int targetMonth = targetDate.getMonthValue();
        Long userId = user.getUserId();
        MonthlyReport reportData = null;
        List<MonthlyReport> reportDataList = monthlyReportRepository.findByIdAndTargetDate(userId,
                targetYear, targetMonth);
        if (!reportDataList.isEmpty()) {
            reportData = reportDataList.get(0);
            reportData.updateAIResponse(keywords, null); //TODO - 두번째 인자 제거 예정
        } else {
            reportData = MonthlyReport.builder()
                    .userId(user.getUserId())
                    .targetDate(targetDate)
                    .keywordsData(keywords)
                    .build();
        }
        monthlyReportRepository.save(reportData);

        log.info("---[SimSimSchedule] 처리 완료 userId = {}", user.getUserId());
        if (!targetDiaries.isEmpty()) {
            log.warn("---[SimSimStatus] targetDiaries[0].SendAble = {}", targetDiaries.getLast().getIsSendAble());
        }
        return new AILetterResponseDTO(aiData, reportData);
    }
}