package com.project.simsim_server.service.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.fastapi.DailyAiResponseDTO;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class DailyAIReplyService {

    private final RestTemplate restTemplate;
    private final DailyAiInfoRepository dailyAiInfoRepository;
    private final DiaryRepository diaryRepository;
    private final String aiUrl = "http://localhost:8000/report";

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

    public AILetterResponseDTO save(AILetterRequestDTO requestDTO) {

        if (requestDTO.getTargetDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("해당 날짜는 AI 편지를 조회할 수 없습니다.");
        }
        // 조회할 일자의 일기 내용을 가져옴
        LocalDateTime startDateTime = requestDTO.getTargetDate().minusDays(1).atStartOfDay();
        LocalDateTime endDateTime = requestDTO.getTargetDate().minusDays(1).atTime(LocalTime.MAX);
        List<Diary> result = diaryRepository.findDiariesByCreatedAtBetweenAndUserId(startDateTime,
                endDateTime, requestDTO.getUserId());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("전날 작성한 일기가 없어 AI 편지를 조회할 수 없습니다.");
        }

        List<DailyAiInfo> prevReply
                = dailyAiInfoRepository.findByCreatedAtBeforeAndUserId(
                        requestDTO.getUserId(), requestDTO.getTargetDate());
        if (!prevReply.isEmpty()) {
            return new AILetterResponseDTO(prevReply.get(0));
        }

        List<String> request = new ArrayList<>();
        for (Diary diary : result) {
            request.add(diary.getContent());
        }

        Map<String, List<String>> requestData = new HashMap<>();
        requestData.put("diarys", request);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonString = objectMapper.writeValueAsString(requestData);
            log.info("JSON Request = {}", jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //AI API 호출
        ResponseEntity<DailyAiResponseDTO> response
                = restTemplate.postForEntity(aiUrl, requestData, DailyAiResponseDTO.class);


        if (response.getStatusCode() != HttpStatus.OK) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AI HttpResponse Code:"
                    + response.getStatusCode());
        }


        DailyAiResponseDTO aiResponse = response.getBody();
        if (aiResponse.getEmotion() == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response is empty.");

        String emotions = aiResponse.getEmotion().toString();

        //분석 내용을 DB에 저장
        DailyAiInfo saveInfo = dailyAiInfoRepository.save(DailyAiInfo.builder()
                .userId(requestDTO.getUserId())
                .targetDate(requestDTO.getTargetDate())
                .diarySummary(aiResponse.getSummary())
                .replyContent(aiResponse.getReply())
                .analyzeEmotions(emotions)
                .build());

        return new AILetterResponseDTO(saveInfo);
    }
}