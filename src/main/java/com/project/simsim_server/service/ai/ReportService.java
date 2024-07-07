package com.project.simsim_server.service.ai;


import com.project.simsim_server.dto.ai.client.EmotionsTotalDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.project.simsim_server.exception.ai.AIErrorCode.EMOTION_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final DailyAiInfoRepository dailyAiInfoRepository;

    public EmotionsTotalDTO weekReport(Long userId, LocalDate targetDate) {
        LocalDate startDate = targetDate.minusDays(14);
        Optional<EmotionsTotalDTO> results = dailyAiInfoRepository.countByUserIdAndTargetDate(userId, startDate, targetDate);
        if (results.isEmpty()) {
            throw new AIException(EMOTION_NOT_FOUND);
        }
        return results.get();
    }
}
