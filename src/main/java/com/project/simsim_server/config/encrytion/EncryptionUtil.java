package com.project.simsim_server.config.encrytion;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {

    @Value("${spring.jwt.key2}")
    private String key;
    private Key secureKey;
    private static final String ALGORITHM = "AES";

    // 사전 확인용 메소드
    @PostConstruct
    public void init() {
        log.warn("------[SimSimInfo] Key 값: {}, 길이: {}-----", this.key, this.key.length());
        log.warn("------[SimSimInfo] Key 바이트 배열 길이: {}-----", this.key.getBytes().length);
        secureKey = generateKey(0);
    }

    //encode
    public String encrypt(String valueToEnc) throws Exception {
//        Key key = generateKey(1);
        log.warn("encrypt 상태: {}, {}", this.key, valueToEnc);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, secureKey);
        byte[] encValue = c.doFinal(valueToEnc.getBytes());
        return Base64.getEncoder().encodeToString(encValue);
    }

    //decode
    public String decrypt(String encryptedValue) throws Exception {
//        Key key = generateKey(2);
        log.warn("decrypt 상태: {}, {}", this.key, encryptedValue);
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, secureKey);
        byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
        byte[] decValue = c.doFinal(decodedValue);

        return new String(decValue);
    }

    private Key generateKey(int num) {
        log.warn("{}, generateKey()의 this.key 상태 = {}", num, this.key);
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

