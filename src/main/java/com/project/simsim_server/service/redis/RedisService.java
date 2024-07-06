package com.project.simsim_server.service.redis;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // TTL 설정 X
    @Transactional
    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    // refrshToken TTL 설정 O
    @Transactional
    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    // redis에 저장된 refreshToken 삭제
    @Transactional
    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    // redis에 저장된 refreshToken 조회
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }

        return (String) values.get(key);
    }

    public boolean checkExistsValue(String value) {
        return value.equals("false");
    }

}
