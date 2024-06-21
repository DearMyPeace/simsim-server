package com.project.simsim_server.service.ai;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DailyAIReplyService {

    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;

    public List<AILetterResponseDTO> findByCreatedDateAndUserIdOrderByCreatedDateDesc
            (Long userId, LocalDate targetDate, int count) {
        LocalDateTime targetDateTime = targetDate.atTime(LocalTime.MAX);
        Pageable pageable = PageRequest.of(0, count);
        List<DailyAiInfo> aiLetters
                = dailyAiInfoRepository.findTopNByCreatedAtBeforeAndUserId(userId, targetDateTime, pageable);
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

    //TODO - AI Api 입력
//    public AILetterResponseDTO save(AILetterRequestDTO requestDTO) {
//
//        // 조회할 일자의 일기 내용을 가져옴
//        LocalDateTime startDateTime = requestDTO.getTargetDate().atStartOfDay();
//        LocalDateTime endDateTime = requestDTO.getTargetDate().atTime(LocalTime.MAX);
//        List<Diary> result = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime,
//                endDateTime, requestDTO.getUserId());
//
//        //AI API 호출
//        //        RestTemplate restTemplate;
//        //        String fastApiUrl = "";
//        //        restTemplate.postForEntity(fastApiUrl, dailyAiRequestDTO, DailyAiResponseDTO);
//
//        //분석 내용을 DB에 저장
//        // ...
//
//        return null;
//    }
}