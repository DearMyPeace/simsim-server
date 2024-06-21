package com.project.simsim_server.service.diary;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.diary.DiaryRequestDTO;
import com.project.simsim_server.dto.diary.DiaryResponseDTO;
import com.project.simsim_server.repository.diary.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    //TODO - 구현 후 처리
    //    private final UserRepository userRepository; : 유저유무 체크하는 로직 모두 추가

    public DiaryResponseDTO findById(Long diaryId) {
        Diary result = diaryRepository.findById(diaryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryId));
        return new DiaryResponseDTO(result);
    }


//    //TODO - UserEntity 생성 후 수정
//    public List<DiaryResponseDTO> findByCreatedDateAndUserEmail(
//            LocalDateTime targetDate,
//            String userEmail
//    ) {
//        User user = userRepository.findByEmail(userEmail);
//        List<Diary> diaries = diaryRepository
//                .findDiariesByCreatedDateAnduserId(targetDate, userId);
//        return diaries.stream()
//                .map(DiaryResponseDTO::new)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }


    public DiaryResponseDTO save(DiaryRequestDTO diaryRequestDTO) {
        return new DiaryResponseDTO(diaryRepository.save(diaryRequestDTO.toEntity()));
    }

    @Transactional
    public DiaryResponseDTO update(Long diaryId, DiaryRequestDTO diaryRequestDTO) {
        Diary result = diaryRepository.findById(diaryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryId));

        Diary updateDiary = result.update(diaryRequestDTO.getContent());
        return new DiaryResponseDTO(updateDiary);
    }

    @Transactional
    public void delete(Long diaryId) {
        Diary result = diaryRepository.findById(diaryId)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryId));
        result.delete();
    }
}
