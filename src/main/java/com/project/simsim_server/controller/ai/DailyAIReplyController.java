package com.project.simsim_server.controller.ai;

import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.exception.ai.AIException;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.project.simsim_server.exception.ai.AIErrorCode.AILETTERS_NOT_FOUND;

@Tag(name = "DailyAIReply", description = "AI 분석 및 답장 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/aiLetters")
@RestController
public class DailyAIReplyController {

    private final DailyAIReplyService dailyAIReplyService;

    /**
     * 원하는 일자, 원하는 갯수에 대해 AI 편지를 조회
     * @param offset
     * @param count
     * @return List<AILetterResponseDTO> 편지 목록
     */
    @GetMapping
    public List<AILetterResponseDTO> getAILetters(
            @RequestParam(required = false) LocalDate offset,
            @RequestParam("total") int count) {

        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);

        if (offset != null) {
            return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, offset, count);
        }
        return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, count);
    }

    /**
     * 오늘 일자에 조회 할 수 있는 AI 편지를 조회(분석 대상은 어제 일자 일기)
     * @param today 오늘일자
     * @return AILetterResponseDTO 어제 일기 요약, 그에 대한 AI의 편지
     */
    @GetMapping("/{today}")
    public AILetterResponseDTO getAILetter(@PathVariable LocalDate today) {
        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        List<AILetterResponseDTO> letter = dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, today, 1);
        if (letter.isEmpty()) {
            throw new AIException(AILETTERS_NOT_FOUND);
        }
        return letter.get(0);
    }


    @PostMapping("/save")
    public AILetterResponseDTO save(@RequestBody AILetterRequestDTO requestDTO) {
        String authentication = getUserIdFromAuthentication();
        Long userId = Long.parseLong(authentication);
        return dailyAIReplyService.save(requestDTO, userId);
    }


    private String getUserIdFromAuthentication() {
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
