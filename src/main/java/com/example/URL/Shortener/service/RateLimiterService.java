package com.example.URL.Shortener.service;

import com.example.URL.Shortener.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService implements IRateLimiterService {
    private final RedisTemplate redisTemplate;
    private final ApplicationProperties applicationProperties;

    @Override
    public boolean isAllowed(String key) {
        try {
            final String redisKey = applicationProperties.getRateLimit().getRateLimitIpKey() + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == 1) {
                redisTemplate.expire(redisKey, applicationProperties.getRateLimit().getWindowSeconds(), TimeUnit.SECONDS);
            }
            return count <= applicationProperties.getRateLimit().getMaxRequests();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
