package com.project.simsim_server.config.encrytion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


@Slf4j
@Component
public class EncryptionUtil {

    @Value("${spring.jwt.key2}")
    private String key;
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 16; // in bytes
    private static final int IV_LENGTH = 12; // in bytes

    // 암호화
    public String encrypt(String valueToEnc) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

        byte[] encryptedBytes = cipher.doFinal(valueToEnc.getBytes(StandardCharsets.UTF_8));
        byte[] encryptedIVAndText = new byte[IV_LENGTH + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedIVAndText, 0, IV_LENGTH);
        System.arraycopy(encryptedBytes, 0, encryptedIVAndText, IV_LENGTH, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encryptedIVAndText);
    }

    // 복호화
    public String decrypt(String encryptedValue) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] iv = new byte[IV_LENGTH];
        System.arraycopy(decodedBytes, 0, iv, 0, IV_LENGTH);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

        byte[] originalText = cipher.doFinal(decodedBytes, IV_LENGTH, decodedBytes.length - IV_LENGTH);

        return new String(originalText, StandardCharsets.UTF_8);
    }
}
