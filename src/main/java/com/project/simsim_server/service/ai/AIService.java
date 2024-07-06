package com.project.simsim_server.service.ai;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.fastapi.DailyAiEmotionResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiLetterRequestDTO;
import com.project.simsim_server.dto.ai.fastapi.DiaryContentDTO;
import com.project.simsim_server.dto.ai.fastapi.DiarySummaryDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.ai.AIErrorCode.AIRESPONE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;
    private final String AI_LETTER_URL = "http://127.0.0.1:8000/ai/v1/letter";
    private final String AI_EMOTION_URL = "http://127.0.0.1:8000/ai/v1/emotion";
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
        log.warn("----[분석할 페르소나] {} : {}", user.getEmail(), user.getPersona());

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
                    .positive(convertStringToList(info.getAnalyzePositive()))
                    .neutral(convertStringToList(info.getAnalyzeNeutral()))
                    .negative(convertStringToList(info.getAnalyzeNegative()))
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
        ResponseEntity<String> response
                = restTemplate.postForEntity(AI_LETTER_URL, requestData, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestLetter AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] requestLetter AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());
        String letter = response.getBody();
        if (letter == null || letter.isEmpty()) {
            log.error("---[SimSimSchedule] requestLetter AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        return letter;
    }


    /**
     * 3. 일기 요약 API 호출
     * @param user
     * @param diaries
     * @return
     */
    public String requestDiarySummary(Users user, List<DiaryContentDTO> diaries) {
        ResponseEntity<String> response
                = restTemplate.postForEntity(AI_SUMMARY_URL, diaries, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestDiarySummary AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] requestDiarySummary AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());
        String summary = response.getBody();
        if (summary == null || summary.isEmpty()) {
            log.error("---[SimSimSchedule] requestDiarySummary AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        return summary;
    }


    /**
     * 4. 감정 분석 API 호출
     * @param user
     * @param diaries
     * @return
     */
    public DailyAiEmotionResponseDTO requestEmotion(Users user, List<DiaryContentDTO> diaries) {
        ResponseEntity<DailyAiEmotionResponseDTO> response
                = restTemplate.postForEntity(AI_EMOTION_URL, diaries, DailyAiEmotionResponseDTO.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] requestEmotion AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] requestEmotion AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());
        DailyAiEmotionResponseDTO emotions = response.getBody();
        if (emotions == null || emotions.getPositive() == null
                || emotions.getNegative() == null || emotions.getNeutral() == null) {
            log.error("---[SimSimSchedule] requestEmotion AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }
        return emotions;
    }


    /**
     * AI 응답에 DB 저장 처리
     * @param user
     * @param targetDate
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public DailyAiInfo processUser(Users user, LocalDate targetDate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        // 예외 처리
        List<Diary> targetDiaries = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime, endDateTime, user.getUserId());
        if (targetDiaries.isEmpty()) {
            log.info("---[SimSimSchedule] 해당 날짜({})에 작성한 일기가 없는 회원 userId = {}", targetDate, user.getUserId());
            return null;
        }

        // AI 요청 정보 생성
        DailyAiLetterRequestDTO requestData = generateRequestData(user, targetDate, targetDiaries);

        // AI 요청
        String letter = requestLetter(user, requestData); // AI_LETTER_URL 호출
        DailyAiEmotionResponseDTO emotions = requestEmotion(user, requestData.getDiaries()); //AI_EMONTION_URL 호출
        String summary = requestDiarySummary(user, requestData.getDiaries()); // AI_SUMMARY_URL 호출
        if (letter == null || emotions == null || summary == null) {
            throw new AIException(AIRESPONE_NOT_FOUND);
        }

        DailyAiInfo saveData = dailyAiInfoRepository.save(DailyAiInfo.builder()
                .userId(user.getUserId())
                .targetDate(targetDate)
                .diarySummary(summary)
                .replyContent(letter)
                .analyzePositive(emotions.getPositive().toString())
                .analyzePositiveTotal(emotions.getPositive_total())
                .analyzeNeutral(emotions.getNeutral().toString())
                .analyzePositiveTotal(emotions.getNeutral_total())
                .analyzeNegative(emotions.getNegative().toString())
                .analyzePositiveTotal(emotions.getNegative_total())
                .replyStatus("N")
                .isFirst(false)
                .build());

        log.info("---[SimSimSchedule] 처리 완료 userId = {}", user.getUserId());
        return saveData;
    }

    private static List<Integer> convertStringToList(String str) {
        return Arrays.stream(str.replaceAll("\\[|\\]", "").split(",\\s*"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
