package com.example.URL.Shortener.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.example.URL.Shortener.constants.Constants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService implements IRateLimiterService {
    private final RedisTemplate redisTemplate;

    @Override
    public boolean isAllowed(String key) {
        try {
            final String redisKey = RATE_LIMIT_IP_KEY + key;
            Long count = redisTemplate.opsForValue().increment(redisKey);
            if (count == 1) {
                redisTemplate.expire(redisKey, TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
            }
            return count <= MAXIMUM_HITS_LIMIT;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }
}
