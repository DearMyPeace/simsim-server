package com.project.simsim_server.service.report;


import com.project.simsim_server.domain.ai.MonthlyReport;
import com.project.simsim_server.dto.ai.client.AIMonthlyResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.MonthlyReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.project.simsim_server.exception.ai.AIErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final MonthlyReportRepository monthlyReportRepository;

    public List<AIMonthlyResponseDTO> findByuserIdAndTargetDate(
            Long userId, String targetDate
    ) {
        if (isFuturDay(targetDate)) {
            throw new AIException(AI_NOT_INVALID_DATE);
        }
        int targetYear = Integer.parseInt(targetDate.substring(0, 4));
        int targetMonth = Integer.parseInt(targetDate.substring(4, 6));
        log.info("---[SimSimInfo] 월간 레포트 targetYear : {}, targetMonth : {}", targetYear, targetMonth);

        List<MonthlyReport> monthlyReportList
                = monthlyReportRepository.findByIdAndTargetDate(userId, targetYear, targetMonth);
        if (monthlyReportList.isEmpty()) {
            throw new AIException(REPORT_NOT_FOUND);
        }
        String jsonData = monthlyReportList.getFirst().getKeywordsData();
        List<AIMonthlyResponseDTO> responseList = AIMonthlyResponseDTO.convertFromJSONtoObject(jsonData);
        if (responseList.isEmpty()) {
            throw new AIException(REPORT_NOT_FOUND);
        }

        //TODO - 추후 삭제
        for(AIMonthlyResponseDTO responseDTO : responseList) {
            log.info("---[SimSimInfo] keyword={}, rate={}, comment={}",
                    responseDTO.getKeyword(), responseDTO.getRate(), responseDTO.getComment());
        }
        return responseList;
    }

    private Boolean isFuturDay(String targetDate) {
        YearMonth targetYearMonth = YearMonth.parse(targetDate, DateTimeFormatter.ofPattern("yyyyMM"));
        YearMonth currentYearMonth = YearMonth.now();
        return targetYearMonth.isAfter(currentYearMonth);
    }
}
