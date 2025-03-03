package com.project.simsim_server.controller.ai;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.dto.ai.client.WeekEmotionsResponseDTO;
import com.project.simsim_server.dto.ai.client.WeekSummaryResponseDTO;
import com.project.simsim_server.service.report.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Report", description = "레포트 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {

    private final ReportService reportService;
    private final AuthenticationService authenticationService;

    @GetMapping("/week")
    public WeekEmotionsResponseDTO getWeeksReportEmotions(@RequestParam("targetDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate targetDate) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return reportService.weekReportEmotions(userId, targetDate);
    }

    @GetMapping("/week/{targetDate}")
    public WeekSummaryResponseDTO getWeeksReportSummary(@PathVariable("targetDate") LocalDate targetDate) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return reportService.weekReportSummary(userId, targetDate);
    }
}
