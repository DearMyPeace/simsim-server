package com.project.simsim_server.service.diary;

import com.project.simsim_server.domain.ai.DailyAiInfo;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.diary.DiaryCountResponseDTO;
import com.project.simsim_server.dto.diary.DiaryDailyResponseDTO;
import com.project.simsim_server.dto.diary.DiaryRequestDTO;
import com.project.simsim_server.dto.diary.DiaryResponseDTO;
import com.project.simsim_server.exception.dairy.DiaryException;
import com.project.simsim_server.repository.ai.DailyAiInfoRepository;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static com.project.simsim_server.exception.dairy.DiaryErrorCode.LIMIT_EXCEEDED;

@RequiredArgsConstructor
@Service
public class DiaryService {

    @Getter
    private final int MAX_DIARIES_PER_DAY = 3;
    private final DiaryRepository diaryRepository;
    private final DailyAiInfoRepository dailyAiInfoRepository;

    public DiaryDailyResponseDTO findByCreatedDate(LocalDate targetDate, Long userId) {
        boolean sendStatus = false;
        List<Diary> diaries = diaryRepository
                .findByCreatedAtAndUserId(userId, targetDate);
        List<DiaryResponseDTO> list = diaries.stream()
                .map(DiaryResponseDTO::new)
                .toList();

        List<DailyAiInfo> aireply = dailyAiInfoRepository.findByCreatedAtAndUserId(userId, targetDate);
        if (aireply.size() == 1 && !aireply.getFirst().isFirst() || aireply.size() >= 2) {
            sendStatus = true;
        }

        return DiaryDailyResponseDTO.builder()
                .sendStatus(sendStatus)
                .diaries(list)
                .build();
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

        List<Diary> todayDiaries
                = diaryRepository.findByCreatedAtAndUserId(userId, diaryRequestDTO.getCreatedDate().toLocalDate());
        if (todayDiaries.size() == MAX_DIARIES_PER_DAY) {
            throw new DiaryException(LIMIT_EXCEEDED);
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
