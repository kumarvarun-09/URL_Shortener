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

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlService implements IUrlService {
    private final UrlRepository urlRepository;
    private final Base62Encoder base62Encoder;

    private final String BASE_URL = "http://localhost:8080/url/";

    @Override
    public String shortenUrl(String originalUrl) {
        if (originalUrl == null || originalUrl.isEmpty()) {
            throw new IllegalArgumentException("URL cannot be empty");
        }
        UrlMapping urlMapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .createdAt(LocalDateTime.now())
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
        return BASE_URL + shortCode;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        UrlMapping urlMapping = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found for short code " + shortCode));

        if (urlMapping.getExpiryAt() != null
                && urlMapping.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException(shortCode);
        }
        log.info("For shortcode: {} , Original URL: {}", shortCode, urlMapping.getOriginalUrl());
        return urlMapping.getOriginalUrl();
    }
}
