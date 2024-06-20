package com.project.simsim_server.controller;

import com.project.simsim_server.dto.DiaryRequestDTO;
import com.project.simsim_server.dto.DiaryResponseDTO;
import com.project.simsim_server.service.DiaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/diary")
@RestController
public class DiaryController {

    private final DiaryService diaryService;

    /**
     * 유저가 특정 일기 하나만을 클릭하여 상세 내용을 조회
     * @param diaryPk
     * @return DiaryResponseDTO (하나의 일기의 상세 내용) / 데이터가 없는 경우 에러
     */
    @GetMapping("/{diaryPk}")
    public DiaryResponseDTO findById(
            @PathVariable Long diaryPk
    ) {
        return diaryService.findById(diaryPk);
    }


    /**
     * 유저가 선택한 일자의 일기 내용 전체를 조회
     * @param targetDate
     * @return List<DiaryResponseDTO> / 데이터가 없는 경우 List의 길이가 0
     */
//    @GetMapping("/{targetDate}")
//    public List<DiaryResponseDTO> findByCreatedDateAndUserEmail(
//            @RequestParam String userEmail,
//            @PathVariable LocalDateTime targetDate) {
//        return diaryService.findByCreatedDateAndUserEmail(targetDate, userEmail);
//    }


    /**
     * 유저가 작성한 하나의 일기를 DB에 저장
     * @param diaryRequestDTO
     * @return dairyResponseDTO / 데이터가 없는 경우 에러
     */
    @PostMapping("/save")
    public DiaryResponseDTO save(
            @RequestBody DiaryRequestDTO diaryRequestDTO
    ) {
        return diaryService.save(diaryRequestDTO);
    }



    /**
     * 유저가 일기 상세 페이지에서 수정한 내용을 DB에 저장
     * @param diaryPk
     * @param diaryRequestDTO
     * @return dairyResponseDTO / 데이터가 없는 경우 에러
     */
    @PatchMapping("/{diaryPk}")
    public DiaryResponseDTO updateDiary(
            @PathVariable Long diaryPk,
            @RequestBody DiaryRequestDTO diaryRequestDTO
    ) {
        return diaryService.updateDiary(diaryPk, diaryRequestDTO);
    }


    /**
     * 유저가 일기를 삭제(실제 데이터가 DB에서 삭제되는 것이 아니라 deleteYn의 값이 "N"으로 변경)
     * @param diaryPk
     */
    @DeleteMapping("/{diaryPk}")
    public void deleteDiary(@PathVariable Long diaryPk) {

        diaryService.deleteDiary(diaryPk);
    }
}
