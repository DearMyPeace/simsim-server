package com.project.simsim_server.service.ai;


import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.dto.ai.client.AnalyzeMaxInfoDTO;
import com.project.simsim_server.dto.ai.client.WeekEmotionsResponseDTO;
import com.project.simsim_server.dto.ai.client.WeekSummaryResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.project.simsim_server.exception.ai.AIErrorCode.EMOTION_NOT_FOUND;
import static com.project.simsim_server.exception.ai.AIErrorCode.REPORT_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReportService {

    private final DailyAiInfoRepository dailyAiInfoRepository;

    public WeekEmotionsResponseDTO weekReportEmotions(Long userId, LocalDate targetDate) {
        log.warn("---[SimSimInfo] 레포트 감정 집계 시작 userId : {}, targetdate : {}", userId, targetDate);

        LocalDate startDate = targetDate.minusDays(14);

        log.warn("---[SimSimInfo] 레포트 감정 집계 시작일 : {}, 종료일 : {}", startDate, targetDate);

        Optional<WeekEmotionsResponseDTO> results = dailyAiInfoRepository.countByUserIdAndTargetDate(userId, startDate, targetDate);
        log.warn("---[SimSimInfo] 레포트 감정 집계 결과 : {}", results.get());
        if (results.isEmpty()) {
            log.error("---[SimSimInfo] 레포트 감정 집계 에러 : 감정 분석 결과가 비어 있음");
            throw new AIException(EMOTION_NOT_FOUND);
        }


        log.warn("---[SimSimInfo] 레포트 감정 집계 종료 userId : {}, targetdate : {}", userId, targetDate);
        return results.get();
    }

    public WeekSummaryResponseDTO weekReportSummary(Long userId, LocalDate targetDate) {
        log.warn("---[SimSimInfo] 레포트 요약 시작 userId : {}, targetdate : {}", userId, targetDate);

        LocalDate startDate = targetDate.minusDays(14);

        log.warn("---[SimSimInfo] 레포트 요약 시작일 : {}, 종료일 : {}", startDate, targetDate);

        List<AnalyzeMaxInfoDTO> positiveInfo
                = dailyAiInfoRepository.findAllByUserIdAndAnalyzePositiveTotal(userId, startDate, targetDate);
        List<AnalyzeMaxInfoDTO> neutralInfo
                = dailyAiInfoRepository.findAllByUserIdAndAnalyzeNeutralTotal(userId, startDate, targetDate);
        List<AnalyzeMaxInfoDTO> negativeInfo
                = dailyAiInfoRepository.findAllByUserIdAndAnalyzeNegativeTotal(userId, startDate, targetDate);

        log.warn("---[SimSimInfo] 레포트 긍정 : {}", positiveInfo);
        log.warn("---[SimSimInfo] 레포트 중립 : {}", neutralInfo);
        log.warn("---[SimSimInfo] 레포트 부정 : {}", negativeInfo);

        // 동일한 점수인 경우, 가장 처음에 작성한 내용을 가져오기
        Optional<DailyAiInfo> positiveSummary = dailyAiInfoRepository.findById(positiveInfo.getFirst().getAiId());
        Optional<DailyAiInfo> neutralSummary = dailyAiInfoRepository.findById(neutralInfo.getFirst().getAiId());
        Optional<DailyAiInfo> negativeSummary = dailyAiInfoRepository.findById(negativeInfo.getFirst().getAiId());

        log.warn("---[SimSimInfo] 레포트 긍정 요약 : {}", positiveSummary.get());
        log.warn("---[SimSimInfo] 레포트 중립 요약 : {}", neutralSummary.get());
        log.warn("---[SimSimInfo] 레포트 부정 요약 : {}", negativeSummary.get());

        if (positiveSummary.isEmpty() || neutralSummary.isEmpty() || negativeSummary.isEmpty()) {
            log.error("---[SimSimInfo] 레포트 요약 에러 : 요약 결과가 비어 있음");
            throw new AIException(REPORT_NOT_FOUND);
        }

        log.warn("---[SimSimInfo] 레포트 요약 종료 userId : {}, targetdate : {}", userId, targetDate);
        return WeekSummaryResponseDTO.builder()
                .positiveDate(positiveInfo.getFirst().getMaxDate())
                .positiveTotalCnt(positiveInfo.getFirst().getEmotionTotal())
                .positiveSummary(positiveSummary.get().getDiarySummary())
                .neutralDate(neutralInfo.getFirst().getMaxDate())
                .neutralTotalCnt(neutralInfo.getFirst().getEmotionTotal())
                .neutralSummary(neutralSummary.get().getDiarySummary())
                .negativeDate(negativeInfo.getFirst().getMaxDate())
                .negativeTotalCnt(negativeInfo.getFirst().getEmotionTotal())
                .negativeSummary(negativeSummary.get().getDiarySummary())
                .build();
    }
}
