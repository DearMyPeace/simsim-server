package com.project.simsim_server.service;

import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.dto.DiaryRequestDTO;
import com.project.simsim_server.dto.DiaryResponseDTO;
import com.project.simsim_server.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryResponseDTO findById(Long diaryPk) {
        Diary result = diaryRepository.findById(diaryPk)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryPk));
        return new DiaryResponseDTO(result);
    }


//    //TODO - UserEntity 생성 후 수정
//    public List<DiaryResponseDTO> findByCreatedDateAndUserEmail(
//            LocalDateTime targetDate,
//            String userEmail
//    ) {
//        User user = userRepository.findByEmail(userEmail);
//        List<Diary> diaries = diaryRepository
//                .findDiariesByCreatedDateAndUserPk(targetDate, userPk);
//        return diaries.stream()
//                .map(DiaryResponseDTO::new)
//                .collect(Collectors.toCollection(ArrayList::new));
//    }


    public DiaryResponseDTO save(DiaryRequestDTO diaryRequestDTO) {
        return new DiaryResponseDTO(diaryRepository.save(diaryRequestDTO.toEntity()));
    }

    @Transactional
    public DiaryResponseDTO updateDiary(Long diaryPk, DiaryRequestDTO diaryRequestDTO) {
        Diary result = diaryRepository.findById(diaryPk)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryPk));

        Diary updateDiary = result.update(diaryRequestDTO.getContent());
        return new DiaryResponseDTO(updateDiary);
    }

    @Transactional
    public void deleteDiary(Long diaryPk) {
        Diary result = diaryRepository.findById(diaryPk)
                .orElseThrow(() ->
                        new IllegalArgumentException("해당 일기가 존재하지 않습니다. 일기번호 : " + diaryPk));
        result.delete();
    }
}
