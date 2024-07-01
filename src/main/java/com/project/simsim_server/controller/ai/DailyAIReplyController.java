package com.project.simsim_server.controller.ai;

import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/aiLetters")
@RestController
public class DailyAIReplyController {

    private final DailyAIReplyService dailyAIReplyService;

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

    @PostMapping("/save")
    public AILetterResponseDTO saveAiResult(@RequestBody AILetterRequestDTO requestDTO) {

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
