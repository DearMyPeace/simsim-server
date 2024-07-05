package com.project.simsim_server.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Grade;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.DiarySummaryResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiRequestDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DiaryContentDTO;
import com.project.simsim_server.dto.ai.fastapi.DiarySummaryDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.simsim_server.exception.ai.AIErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class DailyAIReplyService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;
    private final String AI_URL = "http://localhost:8000/report";
    private final UsersRepository usersRepository;

    public List<AILetterResponseDTO> findByCreatedDateAndUserIdOrderByCreatedDateDesc
            (Long userId, LocalDate targetDate, int count) {
        LocalDateTime targetDateTime = targetDate.atTime(LocalTime.MAX);
        Pageable pageable = PageRequest.of(0, count);
        List<DailyAiInfo> aiLetters
                = dailyAiInfoRepository.findTopNByCreatedAtBeforeAndUserId(userId, targetDateTime, pageable);
        if (aiLetters.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return aiLetters
                .stream()
                .map(AILetterResponseDTO::new)
                .toList();
    }

    public List<AILetterResponseDTO> findByCreatedDateAndUserIdOrderByCreatedDateDesc
            (Long userId, int count) {
        LocalDateTime targetDateTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, count);
        List<DailyAiInfo> aiLetters
                = dailyAiInfoRepository.findTopNByCreatedAtBeforeAndUserId(userId, targetDateTime, pageable);
        return aiLetters
                .stream()
                .map(AILetterResponseDTO::new)
                .toList();
    }

    @Transactional
    public AILetterResponseDTO save(AILetterRequestDTO requestDTO, Long userId) {

        // 유효하지 않은 일자 예외처리
        if (requestDTO.getTargetDate().isAfter(LocalDate.now())) {
            throw new AIException(AI_NOT_INVALID_DATE);
        }

        Users user = usersRepository.findByIdAndUserStatus(userId)
                .orElseThrow(() -> new AIException(AI_MAIL_FAIL));

        // 해당 유저의 해당 일자 AI 답장 정보 조회
        List<DailyAiInfo> aiInfo =
                dailyAiInfoRepository.findByCreatedAtAndUserId(userId, requestDTO.getTargetDate());

        // 일반 등급이면서 분석 대상 날짜가 동일하면 예외 처리
        if (user.getGrade() == Grade.GENERAL
                && !requestDTO.getTargetDate().atStartOfDay()
                .isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.NOON))) {
            throw new AIException(NOT_MEET_USER_GRADE);
        }

        // 기존에 생성된 데이터가 있으면 반환
        if (!aiInfo.isEmpty()) {
            DailyAiInfo info = aiInfo.getFirst().updateReplyStatus("Y");
            DailyAiInfo saveInfo = dailyAiInfoRepository.save(info);
            return new AILetterResponseDTO(saveInfo);
        }

        // 기존에 생성된 데이터가 없으면 생성
        LocalDateTime startDateTime = requestDTO.getTargetDate().atStartOfDay();
        LocalDateTime endDateTime = requestDTO.getTargetDate().atTime(LocalTime.MAX);
        try {
            DailyAiInfo dailyAiInfo = processUser(user, requestDTO.getTargetDate(), startDateTime, endDateTime);
            if (dailyAiInfo == null) {
                throw new AIException(AI_MAIL_FAIL);
            }
            return new AILetterResponseDTO(dailyAiInfo);
        } catch (Exception e) {
            log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
            throw new AIException(AI_MAIL_FAIL);
        }
    }


    @Transactional
    public void saveAuto() {
        // 모든 회원 조회
//        List<Users> userList = usersRepository.findAllAndUserStatus();
        Long testId = 5L;
        Optional<Users> usersOptional = usersRepository.findById(testId);
        Users user = usersOptional.get();

        // 전날 일기 목록 조회
//        LocalDate targetDate = LocalDate.now().minusDays(1);
//        LocalDateTime startDateTime = LocalDate.now().minusDays(1).atStartOfDay();
//        LocalDateTime endDateTime = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

//        for (Users user : userList) {
//            try {
//                processUser(user, targetDate, startDateTime, endDateTime);
//            } catch (Exception e) {
//                log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
//            }
//        }
            LocalDate targetDate = LocalDate.of(2024, 6, 1);

            for (int i = 1; i <= 30; i++){
                LocalDateTime startDateTime = targetDate.atStartOfDay();
                LocalDateTime endDateTime = targetDate.atTime(LocalTime.MAX);
                try {
                    processUser(user, targetDate, startDateTime, endDateTime);
                    targetDate = targetDate.plusDays(1);
                } catch (Exception e) {
                    log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
                }
            }
    }


    private DailyAiInfo processUser(Users user, LocalDate targetDate, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Diary> targetDiaries = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime, endDateTime, user.getUserId());

        if (targetDiaries.isEmpty()) {
            log.info("---[SimSimSchedule] 해당 날짜({})에 작성한 일기가 없는 회원 userId = {}", targetDate, user.getUserId());
            return null;
        }

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

        List<DiarySummaryDTO> summaries= new ArrayList<>();
        // 감정 정보
        for (DailyAiInfo info : summaryList) {
            summaries.add(DiarySummaryDTO.builder()
                    .date(info.getTargetDate())
                    .content(info.getReplyContent())
                    .emotion(convertStringToList(info.getAnalyzeEmotions()))
                    .build());
        }

        DailyAiRequestDTO requestData = DailyAiRequestDTO.builder()
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

        // AI 요청
        ResponseEntity<DailyAiResponseDTO> response = restTemplate.postForEntity(AI_URL, requestData, DailyAiResponseDTO.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("---[SimSimSchedule] AI 응답 실패 userId = {}", user.getUserId());
            return null;
        }

        log.warn("---[SimSimSchedule] AI 응답 내용 {},  userId = {}", response.getBody(), user.getUserId());


        DailyAiResponseDTO aiResponse = response.getBody();
        if (aiResponse == null || aiResponse.getEmotion() == null) {
            log.error("---[SimSimSchedule] AI 응답 내용 없음 userId = {}", user.getUserId());
            return null;
        }

        String emotions = aiResponse.getEmotion().toString();

        DailyAiInfo saveData = dailyAiInfoRepository.save(DailyAiInfo.builder()
                .userId(user.getUserId())
                .targetDate(targetDate)
                .diarySummary(aiResponse.getSummary())
                .replyContent(aiResponse.getReply())
                .analyzeEmotions(emotions)
                .replyStatus("N")
                .isFirst(false)
                .build());

        log.info("---[SimSimSchedule] 처리 완료 userId = {}", user.getUserId());
        return saveData;
    }

    public List<DiarySummaryResponseDTO> findByMonthAndUserId(String year, String month, Long userId) {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        log.warn("서머리 조회 시작 일자 ={}", startDate);
        log.warn("서머리 조회 끝 일자 ={}", endDate);

        List<DailyAiInfo> results
                = dailyAiInfoRepository.findAllSummaryByDate(startDate, endDate, userId);

        for (DailyAiInfo result : results) {
            log.warn(result.toString());
        }

        return results.stream()
                .map(DiarySummaryResponseDTO::new)
                .toList();
    }

    public AILetterResponseDTO findByDateAndUserId(String year, String month, String day, Long userId) {
        LocalDate targetDate
                = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        List<DailyAiInfo> results = dailyAiInfoRepository.findByCreatedAtAndUserId(userId, targetDate);
        DailyAiInfo target = results.get(0).updateReplyStatus("Y");
        dailyAiInfoRepository.save(target);
        return new AILetterResponseDTO(results.getFirst());
    }

    public AILetterResponseDTO findByIdAndUserId(Long id, Long userId) {
        return dailyAiInfoRepository.findByAiIdAndUserId(id, userId)
                .map((entitiy) -> {
                    entitiy.updateReplyStatus("Y");
                    dailyAiInfoRepository.save(entitiy);
                    return new AILetterResponseDTO(entitiy);
                })
                .orElseThrow(() -> new AIException(AILETTERS_NOT_FOUND));
    }

    public static List<Integer> convertStringToList(String str) {
        return Arrays.stream(str.replaceAll("\\[|\\]", "").split(",\\s*"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}