package com.project.simsim_server.config.encrytion;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@Component
@NoArgsConstructor
public class EncryptionUtil {

    @Value("${spring.jwt.key2}")
    private String key;
    private Key securekey;
    private static final String ALGORITHM = "AES";

    // 사전 확인용 메소드
    public void init() {
        log.warn("Key 값: {}", key);
        this.securekey = generateKey();
    }

    // encode
    public String encrypt(String valueToEnc) throws Exception {
        log.warn("encrypt 상태: {}", this.key);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, this.securekey);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());

        return Base64.getEncoder().encodeToString(encValue);
    }

    //decode
    public String decrypt(String encryptedValue) throws Exception {
        log.warn("decrypt 상태: {}", this.key);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, this.securekey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }

    private Key generateKey() {
        return new SecretKeySpec(key.getBytes(), ALGORITHM);
    }

    public boolean isBase64(String data) {
        try {
            Base64.getDecoder().decode(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

