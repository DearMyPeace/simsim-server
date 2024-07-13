package com.project.simsim_server.config.schedule;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import com.project.simsim_server.service.ai.AIService;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import static com.project.simsim_server.exception.ai.AIErrorCode.AI_MAIL_FAIL;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIBatchService {

    private final DailyAiInfoRepository dailyAiInfoRepository;

    private final UsersRepository usersRepository;

    private final DailyAIReplyService dailyAIReplyService;

    private final AIService aiService;

    @Transactional
    public void deleteFirstReply() {
        log.info("--[SimSim Schedule] 안내 메일 DB 삭제 시작");

        LocalDate standardDay = LocalDate.now().minusDays(2);
        List<DailyAiInfo> firstReplies = dailyAiInfoRepository.findByFirstReply();
        for (DailyAiInfo info : firstReplies) {
            if (info.getTargetDate().isBefore(standardDay)) {
                log.info("--[SimSim Schedule] 삭제 aiId : {}, targetDate : {}", info.getAiId(), info.getTargetDate());
                dailyAiInfoRepository.deleteById(info.getAiId());

            }
        }
        log.info("--[SimSim Schedule] 안내 메일 DB 삭제 종료");
    }

    @Transactional
    public void generateDailyAiInfo() {
        log.info("--[SimSim Schedule] 편지 답장 생성 시작");
        List<Users> allUsers = usersRepository.findAll();
        YearMonth ym = YearMonth.of(2024, 5);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = LocalDate.now();
        AILetterRequestDTO requestDTO =
                AILetterRequestDTO.builder()
                        .targetDate(startDate)
                        .build();
        for (Users user : allUsers) {
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                requestDTO.setTargetDate(currentDate);
                saveAuto(requestDTO, user.getUserId());
                currentDate = currentDate.plusDays(1);
            }
        }

        log.info("--[SimSim Schedule] 편지 답장 생성 종료");
    }

    @Transactional
    public AILetterResponseDTO saveAuto(AILetterRequestDTO requestDTO, Long userId) {
        Users user = usersRepository.findByIdAndUserStatus(userId)
                .orElse(null);

        if (user == null) {
            log.warn("---[SimSimINFO] 유저가 유효하지 않습니다 userId = {}", userId);
            return null;
        }

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
                return null;
            } else {
                aiInfo.getFirst().updateReplyStatus("F");
                return null;
            }
        }

        // 기존에 생성된 데이터가 없으면 생성
        LocalDateTime startDateTime = requestDTO.getTargetDate().atStartOfDay();
        LocalDateTime endDateTime = requestDTO.getTargetDate().atTime(LocalTime.MAX);
        try {
            DailyAiInfo dailyAiInfo = aiService.requestToAI(user, requestDTO.getTargetDate(), startDateTime, endDateTime);
            if (dailyAiInfo == null) {
                return null;
            }

            return new AILetterResponseDTO(dailyAiInfo);
        } catch (Exception e) {
            log.error("---[SimSimSchedule] 에러 처리 userId = {}", user.getUserId(), e);
            return null;
        }
    }
}
