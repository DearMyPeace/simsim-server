package com.project.simsim_server.config.schedule;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.diary.Diary;
import com.project.simsim_server.repository.diary.DiaryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DiaryMigrationRunner {

    private final DiaryRepository diaryRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public void migrate() throws Exception {
        List<Diary> diaries = diaryRepository.findAll();

        for (Diary diary : diaries) {
            boolean updated = false;

            // Check if content is already encrypted
            if (!isEncrypted(diary.getContent())) {
                diary.update(encryptionUtil.encrypt(diary.getContent()), diary.getModifiedDate());
                updated = true;
            }

            if (updated) {
                diaryRepository.save(diary);
            }
        }
    }

    private boolean isEncrypted(String value) {
        // Check if the value is Base64 encoded
        try {
            byte[] decodedValue = Base64.getDecoder().decode(value);
            // Try to decrypt the decoded value
            encryptionUtil.decrypt(new String(decodedValue, StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
