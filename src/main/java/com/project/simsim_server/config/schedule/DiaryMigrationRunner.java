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
    public void migrate() throws Exception {
        List<Diary> diaries = diaryRepository.findAll();

        for (Diary diary : diaries) {
            boolean updated = false;


            // Check if email is already encrypted
            if (!isEncrypted(diary.getContent())) {
                diary.update(encryptionUtil.encrypt(diary.getContent()), LocalDateTime.now());
                updated = true;
            }

            if (updated) {
                diaryRepository.save(diary);
            }
        }
    }

    private boolean isEncrypted(String value) {
        // 이미 암호화된 문자열을 구별할 수 있는 방법이 있으면 사용하세요.
        // 예를 들어, 암호화된 문자열은 Base64 형식이므로 이를 활용할 수 있습니다.
        try {
            encryptionUtil.decrypt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
