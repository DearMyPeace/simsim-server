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
                // 암호화가 필요 없는 데이터를 암호화하여 저장
                if (!isEncrypted(entry.getContent())) {
                    String encryptedContent = encryptionUtil.encrypt(entry.getContent());
                    entry.update(encryptedContent, LocalDateTime.now());
                    diaryRepository.save(entry);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to encrypt entry with id " + entry.getDiaryId(), e);
            }
        }
    }

    private boolean isEncrypted(String content) {
        try {
            // 암호화된 데이터인지 확인하려고 복호화를 시도
            encryptionUtil.decrypt(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
