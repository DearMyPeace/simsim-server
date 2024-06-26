package com.project.simsim_server.controller.ai;

import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@CrossOrigin(origins = "*") //TODO - 테스트용, 추후 제거
@RequestMapping("/api/v1/aiLetters")
@RestController
public class DailyAIReplyController {

    private final DailyAIReplyService dailyAIReplyService;

    @GetMapping
    public List<AILetterResponseDTO> getAILetters(
            @RequestParam Long userId,
            @RequestParam(required = false) LocalDate offset,
            @RequestParam("total") int count) {
        if (offset != null) {
            return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, offset, count);
        }
        return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, count);
    }

    @PostMapping("/save")
    public AILetterResponseDTO saveAiResult(@RequestBody AILetterRequestDTO requestDTO) {
        return dailyAIReplyService.save(requestDTO);
    }
}
