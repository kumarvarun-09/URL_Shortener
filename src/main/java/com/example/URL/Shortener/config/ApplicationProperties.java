package com.example.URL.Shortener.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {

    private Redis redis;

    private Analytics analytics;

    private RateLimit rateLimit;

    private String baseUrl;

    private String nullCacheValue;

    @Data
    public static class Redis {
        private String shortUrlPrefix;
        private Long urlTtlSeconds;
        private Long nullTtlSeconds;
        private Long urlTtlMinutes;
        private Long nullTtlMinutes;
    }

    @Data
    public static class Analytics {
        private Long syncIntervalMs;
        private String clickPrefix;
        private String clickIndexKey;
    }

    @Data
    public static class RateLimit {
        private String rateLimitIpKey;
        private Long maxRequests;
        private Long windowSeconds;
    }
}
