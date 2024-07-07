package com.project.simsim_server.controller.ai;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.dto.ai.client.EmotionsTotalDTO;
import com.project.simsim_server.service.ai.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "Report", description = "레포트 서비스")
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
@RestController
public class ReportController {

    private final ReportService reportService;
    private final AuthenticationService authenticationService;

    @GetMapping("/{targetDate}")
    public EmotionsTotalDTO getWeeksReport(@PathVariable LocalDate targetDate) {
        Long userId = authenticationService.getUserIdFromAuthentication();
        return reportService.weekReport(userId, targetDate);
    }
}
