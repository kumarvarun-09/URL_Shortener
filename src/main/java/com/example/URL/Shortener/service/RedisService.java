package com.example.URL.Shortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService implements IRedisService {
    private final RedisTemplate redisTemplate;

    @Override
    public <T> T get(String key, Class<T> entityClass) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            log.info("key: {}, value: {}", key, value);
            return (value != null)
                    ? objectMapper.readValue(value.toString(), entityClass)
                    : null;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void set(String key, Object value, Long ttl) {
        try {
            String json = new ObjectMapper().writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
        }
    }

    @Override
    public void remove(String key) {

    }
}
