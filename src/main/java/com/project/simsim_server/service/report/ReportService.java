package com.project.simsim_server.service.report;


import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.ai.client.AnalyzeMaxInfoDTO;
import com.project.simsim_server.dto.ai.client.WeekEmotionsResponseDTO;
import com.project.simsim_server.dto.ai.client.WeekSummaryResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;

import static com.project.simsim_server.exception.ai.AIErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;
}
