package com.project.simsim_server.config.schedule;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.repository.diary.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiaryMigrationRunner {

    private final DiaryRepository diaryRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public void encryptAndSaveAllEntries() {
        List<Diary> allDiaries = diaryRepository.findAll();

        for (Diary diary : allDiaries) {
            log.warn("---[SimSimInfo] 다이어리 암호화 diaryId : {}", diary.getDiaryId());
            diary.update(diary.getContent(), LocalDateTime.now());
            diaryRepository.save(diary);
        }
    }
}
