package com.project.simsim_server.controller.diary;

import com.project.simsim_server.dto.diary.DiaryCountResponseDTO;
import com.project.simsim_server.dto.diary.DiaryRequestDTO;
import com.project.simsim_server.dto.diary.DiaryResponseDTO;
import com.project.simsim_server.service.diary.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1/diary")
@CrossOrigin(origins = "*")
@RestController
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 유저가 선택한 일자의 일기 내용 전체를 조회
     * @param targetDate
     * @return List<DiaryResponseDTO> / 데이터가 없는 경우 List의 길이가 0
     */
    @GetMapping("/{targetDate}")
    public List<DiaryResponseDTO> findByCreatedDate(
            @PathVariable LocalDate targetDate) {
        return diaryService.findByCreatedDate(targetDate);
    }


    /**
     * 유저가 선택한 연도,월에 대해 일자별 일기 갯수 조회
     * @param year
     * @param month
     * @return
     */
    @GetMapping("/{year}/{month}")
    public List<DiaryCountResponseDTO> countByCreatedYearMonth(
            @PathVariable String year,
            @PathVariable String month
    ) {
        return diaryService.countDiariesByDate(year, month);
    }


    /**
     * 유저가 작성한 하나의 일기를 DB에 저장
     * @param diaryRequestDTO
     * @return dairyResponseDTO / 데이터가 없는 경우 에러
     */
    @PostMapping("/save")
    public DiaryResponseDTO saveDiary(
            @RequestBody DiaryRequestDTO diaryRequestDTO
    ) {
        return diaryService.save(diaryRequestDTO);
    }



    /**
     * 유저가 일기 상세 페이지에서 수정한 내용을 DB에 저장
     * @param diaryId
     * @param diaryRequestDTO
     * @return dairyResponseDTO / 데이터가 없는 경우 에러
     */
    @PatchMapping("/{diaryId}")
    public DiaryResponseDTO updateDiary(
            @PathVariable Long diaryId,
            @RequestBody DiaryRequestDTO diaryRequestDTO
    ) {
        try {
            return diaryService.update(diaryId, diaryRequestDTO);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    /**
     * 유저가 일기를 삭제(실제 데이터가 DB에서 삭제되는 것이 아니라 deleteYn의 값이 "N"으로 변경)
     * @param diaryId
     */
    @DeleteMapping("/{diaryId}")
    public void deleteDiary(@PathVariable Long diaryId) {
        diaryService.delete(diaryId);
    }
}
