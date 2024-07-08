package com.project.simsim_server.config.encrytion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
@Converter(autoApply = true)
public class DatabaseConverter implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;
    private final Pattern base64Pattern = Pattern.compile("^[A-Za-z0-9+/]+={0,2}$");

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return encryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Could not encrypt attribute", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            // 데이터가 Base64 패턴에 맞는지 확인하여 암호화 여부 판단
            if (dbData != null && base64Pattern.matcher(dbData).matches() && dbData.length() > 12) {
                return encryptionUtil.decrypt(dbData);
            } else {
                // 암호화되지 않은 데이터는 그대로 반환
                return dbData;
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not decrypt database column", e);
        }
    }
}
