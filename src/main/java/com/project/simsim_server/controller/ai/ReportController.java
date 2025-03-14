package com.project.simsim_server.controller.ai;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;

import com.project.simsim_server.dto.ai.client.AIMonthlyResponseDTO;
import com.project.simsim_server.service.report.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Report", description = "레포트 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {

    private final ReportService reportService;
    private final AuthenticationService authenticationService;

    /**
     * 월간 키워드 정보 조회
     * @param targetDate year + month ex) 202503
     * @return AIMonthlyResponseDTO 리스트 (rate, keyword, comment)
     */
    @GetMapping("/montly/{targetDate}")
    public List<AIMonthlyResponseDTO> getAIMontlyReport(
            @PathVariable String targetDate
    ) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return reportService.findByuserIdAndTargetDate(userId, targetDate);
    }
}
