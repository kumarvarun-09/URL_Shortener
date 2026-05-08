package com.example.URL.Shortener.service;

import com.example.URL.Shortener.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService implements IAnalyticsService {
    private final StringRedisTemplate redisTemplate;
    private final ApplicationProperties applicationProperties;

    @Override
    public void incrementClick(String shortCode) {
        final String redisKey = applicationProperties.getAnalytics().getClickPrefix() + shortCode;
        try {
            redisTemplate.opsForValue().increment(redisKey);
            redisTemplate.opsForSet().add(applicationProperties.getAnalytics().getClickIndexKey(), shortCode);
            log.info("Incremented click count for shortCode {}", shortCode);
        } catch (Exception e) {
            log.error("Failed to increment click count for shortCode {} \n{}", shortCode, e.getMessage());
        }
    }

    @Override
    public Long getClicks(String shortCode) {
        String clickCount = redisTemplate.opsForValue().get(applicationProperties.getAnalytics().getClickPrefix() + shortCode);
        log.info("Click count for shortCode {} is {}", shortCode, clickCount);
        return clickCount != null ? Long.parseLong(clickCount) : 0L;
    }
}
