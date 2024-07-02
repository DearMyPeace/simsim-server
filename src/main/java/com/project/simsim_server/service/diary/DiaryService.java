package com.project.simsim_server.service.diary;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.diary.DiaryCountResponseDTO;
import com.project.simsim_server.dto.diary.DiaryRequestDTO;
import com.project.simsim_server.dto.diary.DiaryResponseDTO;
import com.project.simsim_server.exception.DiaryLimitExceededException;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private static final int MAX_DIARIES_PER_DAY = 3;

    public List<DiaryResponseDTO> findByCreatedDate(LocalDate targetDate, Long userId) {
        LocalDateTime startDate = targetDate.atStartOfDay();
        LocalDateTime endDate = targetDate.atTime(LocalTime.MAX);
        List<Diary> diaries = diaryRepository
                .findDiariesByCreatedAtBetweenAndUserId(startDate, endDate, userId);
        return diaries.stream()
                .map(DiaryResponseDTO::new)
                .toList();
    }

    public List<DiaryCountResponseDTO> countDiariesByDate(String year, String month, Long userId) {
        YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Object[]> results = diaryRepository.countDiariesByDate(startDate, endDate, userId);
        return results.stream()
                .map(result ->
                        new DiaryCountResponseDTO((LocalDate) result[0], (Long) result[1]))
                .toList();
    }


    public DiaryResponseDTO save(DiaryRequestDTO diaryRequestDTO, Long userId) {
        List<DiaryResponseDTO> todayDiaries
                = findByCreatedDate(diaryRequestDTO.getCreatedDate().toLocalDate(), userId);
        if (todayDiaries.size() == MAX_DIARIES_PER_DAY) {
            throw new DiaryLimitExceededException("금일 작성할 수 있는 일기 갯수를 초과했습니다.");
        }
        diaryRequestDTO.setUserId(userId);
        return new DiaryResponseDTO(diaryRepository.save(diaryRequestDTO.toEntity()));
    }

    @Transactional
    public DiaryResponseDTO update(Long diaryId, DiaryRequestDTO diaryRequestDTO, Long userId) {
        Diary result = diaryRepository.findByIdAndUserId(diaryId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryId));

        if (diaryRequestDTO.getCreatedDate() == null) {
            throw new IllegalArgumentException("일기 등록 일자를 입력해 주세요.");
        } else if (diaryRequestDTO.getModifiedDate() == null) {
            throw new IllegalArgumentException("일기 수정 일자를 입력해 주세요.");
        } else if (diaryRequestDTO.getCreatedDate().isAfter(LocalDateTime.now())
                || diaryRequestDTO.getModifiedDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("유효하지 않은 날짜입니다.");
        }
        Diary updateDiary = result.update(diaryRequestDTO.getContent(), diaryRequestDTO.getModifiedDate());
        return new DiaryResponseDTO(updateDiary);
    }

    @Transactional
    public void delete(Long diaryId, Long userId) {
        Diary result = diaryRepository.findByIdAndUserId(diaryId, userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryId));
        result.delete();
    }
}
