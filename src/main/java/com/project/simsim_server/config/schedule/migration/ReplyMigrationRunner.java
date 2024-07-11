package com.project.simsim_server.config.schedule.migration;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReplyMigrationRunner {

    private final DailyAiInfoRepository dailyAiInfoRepository;

    @Transactional
    public void encryptAndSaveAllEntries() throws Exception {
//        List<DailyAiInfo> allDiaries = dailyAiInfoRepository.findAll();
        List<DailyAiInfo> allReplies = dailyAiInfoRepository.findByUserId(1L);

        for (DailyAiInfo info : allReplies) {
            log.warn("---[SimSimInfo] 일기 요약 및 편지 암호화 diaryId : {}", info.getAiId());
            log.warn("---[SimSimInfo] Before encryption - Diary Summary: {}, Reply Content: {}", info.getDiarySummary(), info.getReplyContent());

            info.updateAiResult(info.getDiarySummary(), info.getReplyContent());
            dailyAiInfoRepository.save(info);

            DailyAiInfo savedInfo = dailyAiInfoRepository.findById(info.getAiId()).orElse(null);
            if (savedInfo != null) {
                log.warn("---[SimSimInfo] After encryption - Diary Summary: {}, Reply Content: {}", savedInfo.getDiarySummary(), savedInfo.getReplyContent());
            }
        }
    }
}
