package com.project.simsim_server.config.encrytion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class DatabaseConverter implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptUtil;

    // 암호화
    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return encryptUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 복호화
    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return encryptUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
