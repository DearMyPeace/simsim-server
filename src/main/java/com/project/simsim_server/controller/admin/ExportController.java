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


@Slf4j
@Tag(name = "Export", description = "관리자 서비스")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/export")
public class ExportController {

    private final ExportService exportService;
    private final AuthenticationService authenticationService;

    @GetMapping("/diary")
    public ResponseEntity<Resource> exportDiary() throws IOException {
        Long userId = authenticationService.getUserIdFromAuthentication();
        log.info("---[SimSimInfo] 유저 {} 일기 Export 요청 시작", userId);

        String fileName = "diary_export_" + LocalDate.now() + ".csv";
        String directory = System.getProperty("java.io.tmpdir"); // 안전한 임시 디렉토리 사용
        String filePath = directory + File.separator + fileName;

        exportService.getDiaries(userId, filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("--- [SimSimError] exportDiary() 해당 파일이 존재하지 않습니다 {}", filePath);
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            log.info("--- [SimSimInfo] exportDiary() 파일이 생성되었습니다 {}", filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .contentLength(file.length())
                    .body(resource);
        } catch (IOException e) {
            log.error("--- [SimSimError] exportDiary() {} 파일 읽기에 실패했습니다", filePath, e);
            throw e;
        }
    }

    @GetMapping("/report")
    public ResponseEntity<Resource> exportReport() throws IOException {
        Long userId = authenticationService.getUserIdFromAuthentication();
        log.info("---[SimSimInfo] 유저 {} AI 응답 Export 요청 시작", userId);

        String fileName = "response_export_" + LocalDate.now() + ".csv";
        String directory = System.getProperty("java.io.tmpdir"); // 안전한 임시 디렉토리 사용
        String filePath = directory + File.separator + fileName;

        exportService.getReponses(userId, filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            log.error("--- [SimSimError] exportResponses() 해당 파일이 존재하지 않습니다 {}", filePath);
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            log.info("--- [SimSimInfo] exportResponses() 파일이 생성되었습니다 {}", filePath);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .contentLength(file.length())
                    .body(resource);
        } catch (IOException e) {
            log.error("--- [SimSimError] exportResponses() {} 파일 읽기에 실패했습니다", filePath, e);
            throw e;
        }
    }
}
