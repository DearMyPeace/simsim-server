package com.project.simsim_server.controller.ai;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.dto.ai.client.AILetterRequestDTO;
import com.project.simsim_server.dto.ai.client.AILetterResponseDTO;
import com.project.simsim_server.dto.ai.client.AIThumsRequestDTO;
import com.project.simsim_server.dto.ai.client.DiarySummaryResponseDTO;
import com.project.simsim_server.service.ai.DailyAIReplyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@Tag(name = "DailyAIReply", description = "AI 분석 및 답장 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/aiLetters")
@RestController
public class DailyAIReplyController {

    private final DailyAIReplyService dailyAIReplyService;
    private final AuthenticationService authenticationService;

    /**
     * id로 일기 조회
     * @param id
     * @return
     */
    @GetMapping
    public AILetterResponseDTO getAILetter(@RequestParam("id") Long id) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return dailyAIReplyService.findByIdAndUserId(id, userId);
    }


    /**
     * 원하는 일자, 원하는 갯수에 대해 AI 편지를 조회
     * @param offset
     * @param count
     * @return List<AILetterResponseDTO> 편지 목록
     */
    @GetMapping("/list")
    public List<AILetterResponseDTO> getAILetters(
            @RequestParam(required = false) LocalDate offset,
            @RequestParam("total") int count) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        if (offset != null) {
            return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, offset, count);
        }
        return dailyAIReplyService.findByCreatedDateAndUserIdOrderByCreatedDateDesc(userId, count);
    }


    /**
     * 월별 일기 조회
     * @param year
     * @param month
     * @return
     */
    @GetMapping("/{year}/{month}")
    public List<DiarySummaryResponseDTO> getSummaryAtMonth(
            @PathVariable String year,
            @PathVariable String month
    ) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return dailyAIReplyService.findByMonthAndUserId(year, month, userId);
    }


    /**
     * 유저가 편지 송신 버튼을 클릭하면 편지 생성
     * @param requestDTO
     * @return AILetterResponseDTO 요약내용, 편지 등
     */
    @PostMapping("/save")
    public AILetterResponseDTO save(@RequestBody AILetterRequestDTO requestDTO) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return dailyAIReplyService.save(requestDTO, userId);
    }



    @PostMapping("/thums")
    public AILetterResponseDTO updateThumsStatus(@RequestBody AIThumsRequestDTO requestDTO) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return dailyAIReplyService.updateThumsStatus(requestDTO, userId);
    }
}
