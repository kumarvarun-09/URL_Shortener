package com.example.URL.Shortener.service;

import com.example.URL.Shortener.exception.UrlExpiredException;
import com.example.URL.Shortener.exception.UrlNotFoundException;
import com.example.URL.Shortener.model.UrlMapping;
import com.example.URL.Shortener.repository.UrlRepository;
import com.example.URL.Shortener.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.URL.Shortener.constants.Constants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService implements IUrlService {
    private final UrlRepository urlRepository;
    private final Base62Encoder base62Encoder;
    private final IRedisService redisService;
    private final IAnalyticsService analyticsService;

    @Override
    public String shortenUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        Long newId = 1L;
        UrlMapping lastRecord = urlRepository.findTopByOrderByIdDesc().orElse(null);
        if (lastRecord != null) {
            newId += lastRecord.getId();
        }
        String shortCode = base62Encoder.encode(newId);
        urlMapping.setShortCode(shortCode);
        urlRepository.save(urlMapping);
        log.info("Shortened URL: {} for original URL: {}", BASE_URL + shortCode, originalUrl);
        redisService.set(SHORT_URL_PREFIX + shortCode, urlMapping, TIME_TO_LIVE_FOR_CACHE);
        return BASE_URL + shortCode;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        UrlMapping urlMapping = null;
        final String shortCodeKey = SHORT_URL_PREFIX + shortCode;
        try {
            urlMapping = redisService.get(shortCodeKey, UrlMapping.class);
        } catch (Exception e) {
            log.error("Redis failed, fallback to DB", e);
        }
        if (urlMapping != null) {
            if (NULL_VALUE.equals(urlMapping.getOriginalUrl())) {
                log.warn("Cache hit, URL for shortcode {} is null", shortCode);
                throw new UrlNotFoundException("Original URL for shortcode not found " + shortCode);
            }
            analyticsService.incrementClick(shortCode);
            log.info("Data found in cache, shortCode: {} , Original URL: {}", shortCode, urlMapping.getOriginalUrl());
            return urlMapping.getOriginalUrl();
        }
        urlMapping = urlRepository.findByShortCode(shortCode).orElse(null);
        if (urlMapping == null) {
            UrlMapping nullTempForCache = UrlMapping.builder()
                    .shortCode(shortCode)
                    .originalUrl(NULL_VALUE)
                    .createdAt(LocalDateTime.now())
                    .clickCount(0L)
                    .build();
            redisService.set(shortCodeKey, nullTempForCache, TIME_TO_LIVE_FOR_CACHE);
            throw new UrlNotFoundException("URL not found for short code " + shortCode);
        }

        if (urlMapping.getExpiryAt() != null
                && urlMapping.getExpiryAt().isBefore(LocalDateTime.now())) {
            redisService.remove(shortCodeKey);
            throw new UrlExpiredException(shortCode);
        }
        redisService.set(shortCodeKey, urlMapping, TIME_TO_LIVE_FOR_CACHE);
        analyticsService.incrementClick(shortCode);
        log.info("Data not in cache, fetching from db, For shortcode: {} , Original URL: {}", shortCode, urlMapping.getOriginalUrl());
        return urlMapping.getOriginalUrl();
    }
}
