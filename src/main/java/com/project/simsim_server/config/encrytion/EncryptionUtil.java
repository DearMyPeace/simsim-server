package com.project.simsim_server.config.encrytion;

import jakarta.annotation.PostConstruct;
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
public class EncryptionUtil {

    @Value("${spring.jwt.key2}")
    private String key;
    private static final String ALGORITHM = "AES";

    // 사전 확인용 메소드
    @PostConstruct
    public void init() {
        log.warn("Key 값: {}", key);
    }

    //encode
    public String encrypt(String valueToEnc) throws Exception {
        Key key = generateKey();
        log.warn("encrypt 상태: {}", key);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());

        return Base64.getEncoder().encodeToString(encValue);
    }

    //decode
    public String decrypt(String encryptedValue) throws Exception {
        Key key = generateKey();
        log.warn("decrypt 상태: {}", key);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
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

