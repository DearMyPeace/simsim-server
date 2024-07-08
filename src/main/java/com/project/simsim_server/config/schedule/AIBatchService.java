package com.project.simsim_server.config.schedule;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AIBatchService {

    private final DailyAiInfoRepository dailyAiInfoRepository;

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
}
