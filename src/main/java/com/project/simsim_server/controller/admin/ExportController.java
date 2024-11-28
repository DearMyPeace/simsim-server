package com.project.simsim_server.controller.admin;

import com.project.simsim_server.config.auth.jwt.AuthenticationService;
import com.project.simsim_server.service.admin.ExportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@Slf4j
@Tag(name = "Export", description = "관리자 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/export")
public class ExportController {

    private final ExportService exportService;
    private final AuthenticationService authenticationService;

    @GetMapping("/diary")
    public ResponseEntity<List<Map<String, Object>>> exportDiary() throws IOException {
        Long userId = authenticationService.getUserIdFromAuthentication();
        log.info("---[SimSimInfo] 유저 {} 일기 Export 요청 시작", userId);

        String fileName = "diary_export_" + LocalDate.now() + ".csv";
        String directory = System.getProperty("java.io.tmpdir"); // 안전한 임시 디렉토리 사용
        String filePath = directory + File.separator + fileName;

        List<Map<String, Object>> diaryData = exportService.getDiaries(userId, filePath);
        log.info("--- [SimSimInfo] 유저 {} 기록 Export 완료 (CSV 파일: {})", userId, filePath);
        return ResponseEntity.ok(diaryData);
    }

    @GetMapping("/report")
    public ResponseEntity<List<Map<String, Object>>> exportReport() throws IOException {
        Long userId = authenticationService.getUserIdFromAuthentication();
        log.info("---[SimSimInfo] 유저 {} AI 응답 Export 요청 시작", userId);

        String fileName = "response_export_" + LocalDate.now() + ".csv";
        String directory = System.getProperty("java.io.tmpdir"); // 안전한 임시 디렉토리 사용
        String filePath = directory + File.separator + fileName;

        List<Map<String, Object>> responsesData = exportService.getReponses(userId, filePath);
        log.info("--- [SimSimInfo] 유저 {} 편지 Export 완료 (CSV 파일: {})", userId, filePath);
        return ResponseEntity.ok(responsesData);
    }
}
