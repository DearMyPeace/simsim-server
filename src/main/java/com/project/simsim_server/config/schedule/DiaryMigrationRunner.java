package com.project.simsim_server.config.schedule;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.repository.diary.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiaryMigrationRunner {

    private final DiaryRepository diaryRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public void encryptAndSaveAllEntries() {
        List<Diary> entries = diaryRepository.findAll();
        for (Diary entry : entries) {
            try {
                String encryptedContent = new EncryptionUtil().encrypt(entry.getContent());
                entry.update(encryptedContent, LocalDateTime.now());
                diaryRepository.save(entry);
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt entry with id " + entry.getDiaryId(), e);
            }
        }
    }
}
