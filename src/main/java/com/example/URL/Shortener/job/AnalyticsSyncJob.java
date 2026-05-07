package com.example.URL.Shortener.job;

import com.example.URL.Shortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

import static com.example.URL.Shortener.constants.Constants.CLICK_INDEX_KEY;
import static com.example.URL.Shortener.constants.Constants.CLICK_PREFIX;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsSyncJob {
    private final StringRedisTemplate redisTemplate;
    private final UrlRepository urlRepository;

    //    @Scheduled(fixedRate = 300000) // fixedRate every X ms regardless of previous completion
    @Scheduled(fixedDelay = 60000) // wait X ms AFTER previous completion
    public void syncClicksToDb() {
        long startingTime = 0L;
        try {
            startingTime = System.currentTimeMillis();
            log.info("Starting AnalyticsSyncJob...");

            Set<String> shortCodeSet = redisTemplate.opsForSet().members(CLICK_INDEX_KEY);
            if (shortCodeSet == null || shortCodeSet.isEmpty()) {
                log.info("No records to process");
                return;
            }
            for (String shortCode : shortCodeSet) {
                final String redisKey = CLICK_PREFIX + shortCode;
                final String redisValue = redisTemplate.opsForValue().get(redisKey);
                log.info("Redis key: {} , value: {}", redisKey, redisValue);
                if (redisValue == null) {
                    continue;
                }
                final Long delta = Long.parseLong(redisValue);
                urlRepository.findByShortCode(shortCode)
                        .ifPresent(entity -> {
                            final Long newClickCount = entity.getClickCount() + delta;
                            entity.setClickCount(newClickCount);
                            urlRepository.save(entity);
                        });
                redisTemplate.delete(redisKey); // Removing clicks data from cache after sync to db
                redisTemplate.opsForSet().remove(CLICK_INDEX_KEY, shortCode);
            }
        } catch (Exception e) {
            log.error("Error while syncing Clicks to DB.", e);
        } finally {
            log.info("AnalyticsSyncJob completed successfully in {} ms", System.currentTimeMillis() - startingTime);
        }
    }
}
