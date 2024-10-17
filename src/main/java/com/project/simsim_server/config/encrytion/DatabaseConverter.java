package com.project.simsim_server.config.encrytion;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Converter(autoApply = true)
public class DatabaseConverter implements AttributeConverter<String, String> {

    private final EncryptionUtil encryptionUtil;

    //application --> database
    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            log.warn("---[SimSimInfo] 해당 데이터를 암호화합니다.");
            return encryptionUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //database --> application
    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if (encryptionUtil.isBase64(dbData)) {
                log.warn("---[SimSimInfo] 해당 데이터는 암호화 되어 있어 디코딩을 진행합니다.");

                log.warn("---[SimSimInfo] 복호화된 문장 = {}", encryptionUtil.decrypt(dbData));

                return encryptionUtil.decrypt(dbData);
            } else {
                log.warn("---[SimSimInfo] 해당 데이터는 평문 입니다.");
                log.warn("---[SimSimInfo] 복호화된 문장 = {}", dbData);
                return dbData;
            }
        } catch (Exception e) {
            // 복호화 실패 시 평문 데이터를 반환
            log.warn("---[SimSimInfo] 예외가 발생하여 평문을 반환합니다.");
            log.warn("---[SimSimInfo] 복호화된 문장 = {}", dbData);
            return dbData;
        }
    }
}
