package com.project.simsim_server.config.schedule.migration;

import com.project.simsim_server.config.encrytion.EncryptionUtil;
import com.project.simsim_server.domain.user.Users;
import com.project.simsim_server.repository.user.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsersMigrationRunner {

    private final UsersRepository userRepository;
    private final EncryptionUtil encryptionUtil;

    @Transactional
    public void migrate() throws Exception {
        List<Users> users = userRepository.findAll();

        for (Users user : users) {
            boolean updated = false;

            // Check if name is already encrypted
            if (!isEncrypted(user.getName())) {
                user.update(encryptionUtil.encrypt(user.getName()));
                updated = true;
            }

            // Check if email is already encrypted
            if (!isEncrypted(user.getEmail())) {
                user.updateEmail(encryptionUtil.encrypt(user.getEmail()));
                updated = true;
            }

            if (updated) {
                userRepository.save(user);
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
