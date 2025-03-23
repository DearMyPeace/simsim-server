package com.project.simsim_server.service.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.DiarySummaryResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

import static com.project.simsim_server.exception.ai.AIErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DailyAIReplyService {

    private final AIService aiService;
    private final UsersRepository usersRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;


    @Transactional
    public AILetterResponseDTO findByIdAndUserId(Long id, Long userId) {
        return dailyAiInfoRepository.findByAiIdAndUserId(id, userId)
                .map((entitiy) -> {
                    entitiy.updateReplyStatus("Y");
//                    dailyAiInfoRepository.save(entitiy);
                    return new AILetterResponseDTO(entitiy);
                })
                .orElseThrow(() -> new AIException(AILETTERS_NOT_FOUND));
    }


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

        log.info("---[SimSimInfo] 요청일자 = {}", requestDTO.getTargetDate());

        Users user = usersRepository.findByIdAndUserStatus(userId)
                .orElseThrow(() -> new AIException(AI_MAIL_FAIL));

        List<Diary> diaries = diaryRepository.findByCreatedAtAndUserId(userId, requestDTO.getTargetDate());
        if (diaries.isEmpty()) {
            log.info("---[SimSimSchedule] 해당 날짜({})에 작성한 일기가 없는 회원 userId = {}", requestDTO.getTargetDate(), user.getUserId());
            throw new AIException(NO_DIARIES);
        }

        // TODO - 추후 삭제
        for (Diary diary : diaries) {
            log.warn("---[SimSimInfo] 금일 일기 userId = {} : {}", diary.getUserId(), diary.getContent());
        }

        boolean hasSendable = diaries.stream()
                .anyMatch(diary -> "Y".equals(diary.getIsSendAble()));

        List<DailyAiInfo> aiInfo =
                dailyAiInfoRepository.findByCreatedAtAndUserId(userId, requestDTO.getTargetDate());

        log.warn("---[SimSimINFO] AI 편지::기존 AI 데이터 개수 ={}, userId = {}",
                aiInfo.size(), user.getUserId());

        // 기존 AI 응답 반환
        if (!hasSendable && !aiInfo.isEmpty() && !aiInfo.getFirst().isFirst()) {
            DailyAiInfo responseInfo = aiInfo.getFirst();
            log.warn("---[SimSimINFO] AI 편지::기존 AI 데이터 ={}, userId = {}",
                    responseInfo.toString(), user.getUserId());
            log.warn("---[SimSimINFO] AI 편지::기존 AI 응답을 반환합니다");

            aiInfo.getFirst().updateReplyStatus("F");
            dailyAiInfoRepository.save(aiInfo.getFirst());
            return new AILetterResponseDTO(responseInfo);
        }

        // AI 응답 새로 생성
        try {
            AILetterResponseDTO responseDTO = aiService.requestToAI(user, requestDTO.getTargetDate(), diaries);
            if (responseDTO == null) {
                throw new AIException(AI_MAIL_FAIL);
            }
            return responseDTO;
        } catch (Exception e) {
            log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
            throw new AIException(AI_MAIL_FAIL);
        }
    }

    @Transactional
    public List<DiarySummaryResponseDTO> findByMonthAndUserId(String year, String month, Long userId) {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        log.warn("서머리 조회 시작 일자 ={}", startDate);
        log.warn("서머리 조회 끝 일자 ={}", endDate);

        List<DailyAiInfo> results
                = dailyAiInfoRepository.findAllSummaryByDate(startDate, endDate, userId);

        Iterator<DailyAiInfo> iterator = results.iterator(); //TODO - 로그 삭제할 때 로직 수정
        while (iterator.hasNext()) {
            DailyAiInfo result = iterator.next();
            if (results.size() > 1) {
                if (result.isFirst()) {
                    log.warn("---[SimSimInfo] 이건 안내이므로 replyStatus를 F 처리합니다 ");
                    result.updateReplyStatus("F");
//                    DailyAiInfo dailyAiInfo = result.updateReplyStatus("F");
//                    dailyAiInfoRepository.save(dailyAiInfo);
                    iterator.remove();
                }
            }
            log.warn(result.toString());
        }

        return results.stream()
                .map(DiarySummaryResponseDTO::new)
                .toList();
    }
}