package com.project.simsim_server.config.schedule;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.user.UsersRepository;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIBatchService {

    private final DailyAiInfoRepository dailyAiInfoRepository;

    private final UsersRepository usersRepository;

    private final DailyAIReplyService dailyAIReplyService;

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
//        for (Users user : allUsers) {
//            dailyAIReplyService.save(requestDTO, user.getUserId());
//            requestDTO.setTargetDate(requestDTO.getTargetDate().plusDays(1));
//        }
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            requestDTO.setTargetDate(currentDate);
            dailyAIReplyService.save(requestDTO, 1L);
            currentDate = currentDate.plusDays(1);
        }

        log.info("--[SimSim Schedule] 편지 답장 생성 종료");
    }
}
