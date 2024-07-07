package com.project.simsim_server.service.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.domain.user.Grade;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.DiarySummaryResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiEmotionResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiLetterRequestDTO;
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
@Service
public class DailyAIReplyService {

    private final AIService aiService;
    private final UsersRepository usersRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;


    public AILetterResponseDTO findByIdAndUserId(Long id, Long userId) {
        return dailyAiInfoRepository.findByAiIdAndUserId(id, userId)
                .map((entitiy) -> {
                    entitiy.updateReplyStatus("Y");
                    dailyAiInfoRepository.save(entitiy);
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

        // 유효하지 않은 일자 예외처리
//        if (requestDTO.getTargetDate().isAfter(LocalDate.now())) {
//            throw new AIException(AI_NOT_INVALID_DATE);
//        }
        Users user = usersRepository.findByIdAndUserStatus(userId)
                .orElseThrow(() -> new AIException(AI_MAIL_FAIL));


        // 해당 유저의 해당 일자 AI 답장 정보 조회
        List<DailyAiInfo> aiInfo =
                dailyAiInfoRepository.findByCreatedAtAndUserId(userId, requestDTO.getTargetDate());

        // 기존에 생성된 데이터가 있으면 반환(안내 편지는 제외 시킴)
        if (!aiInfo.isEmpty()) {
            log.warn("---[SimSimINFO] AI 편지::기존 AI 데이터 개수 ={}, userId = {}",
                    aiInfo.size(), user.getUserId());
            DailyAiInfo responseInfo = aiInfo.getFirst();
            log.warn("---[SimSimINFO] AI 편지::기존 AI 데이터 ={}, userId = {}",
                    responseInfo.toString(), user.getUserId());

            if (!responseInfo.isFirst()) {
                log.warn("---[SimSimINFO] AI 편지::기존 AI 데이터는 안내문이 아니므로 저장된 데이터를 반환합니다. userId = {}",
                        user.getUserId());
                return new AILetterResponseDTO(responseInfo);
            } else { // 기존에 생성된 데이터가 안내 편지면 더 이상 보이지 않게 함
                aiInfo.getFirst().updateReplyStatus("F");
                dailyAiInfoRepository.save(responseInfo);
            }
        }


        // 기존에 생성된 데이터가 없으면 생성
        LocalDateTime startDateTime = requestDTO.getTargetDate().atStartOfDay();
        LocalDateTime endDateTime = requestDTO.getTargetDate().atTime(LocalTime.MAX);
        try {
            DailyAiInfo dailyAiInfo = aiService.processUser(user, requestDTO.getTargetDate(), startDateTime, endDateTime);
            if (dailyAiInfo == null) {
                throw new AIException(AI_MAIL_FAIL);
            }

            //TODO 일반 등급이면서 분석 대상 날짜가 동일하면 예외 처리(동일 날짜가 아닌 12시간 이후로 수정 예정)
            if (user.getGrade() == Grade.GENERAL && !requestDTO.getTargetDate().atStartOfDay()
                .isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.NOON))) {
                throw new AIException(NOT_MEET_USER_GRADE);
            }

            return new AILetterResponseDTO(dailyAiInfo);
        } catch (Exception e) {
            log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
            throw new AIException(AI_MAIL_FAIL);
        }
    }


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
                    log.warn("---[]");
                    log.warn("---[SimSimInfo] 이건 안내이므로 replyStatus를 F 처리합니다 ");
                    DailyAiInfo dailyAiInfo = result.updateReplyStatus("F");
                    dailyAiInfoRepository.save(dailyAiInfo);
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