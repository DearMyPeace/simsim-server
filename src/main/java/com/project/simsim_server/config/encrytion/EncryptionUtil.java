package com.project.simsim_server.config.encrytion;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
public class EncryptionUtil {

    private final String key;
    private final SecretKey secretKey;

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String ENCRYPTION_MODE = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16;
    private static final int GCM_IV_LENGTH = 12;

    /**
     * Secretkey 생성
     */
    public EncryptionUtil(@Value("${spring.jwt.key2}") String key) {
        this.key = key;
        this.secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
    }

    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDataWithIv = new byte[GCM_IV_LENGTH + encryptedData.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, GCM_IV_LENGTH);
        System.arraycopy(encryptedData, 0, encryptedDataWithIv, GCM_IV_LENGTH, encryptedData.length);
        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }
    public String decrypt(String encryptedData) throws Exception {
        byte[] encryptedDataWithIv = Base64.getDecoder().decode(encryptedData);
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedDataWithIv, 0, iv, 0, GCM_IV_LENGTH);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MODE);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        byte[] encryptedDataBytes = new byte[encryptedDataWithIv.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedDataWithIv, GCM_IV_LENGTH, encryptedDataBytes, 0, encryptedDataBytes.length);
        byte[] decryptedData = cipher.doFinal(encryptedDataBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
}
