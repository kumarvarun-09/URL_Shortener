package com.example.URL.Shortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.URL.Shortener.constants.Constants.CLICK_PREFIX;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService implements IAnalyticsService {
    private final StringRedisTemplate redisTemplate;

    @Override
    public void incrementClick(String shortCode) {
        final String redisKey = CLICK_PREFIX + shortCode;
        try {
            redisTemplate.opsForValue().increment(redisKey);
            log.info("Incremented click count for shortCode {}", shortCode);
        } catch (Exception e) {
            log.error("Failed to increment click count for shortCode {} \n{}", shortCode, e.getMessage());
        }
    }

    @Override
    public Long getClicks(String shortCode) {
        String clickCount = redisTemplate.opsForValue().get(CLICK_PREFIX + shortCode);
        log.info("Click count for shortCode {} is {}", shortCode, clickCount);
        return clickCount != null ? Long.parseLong(clickCount) : 0L;
    }
}
