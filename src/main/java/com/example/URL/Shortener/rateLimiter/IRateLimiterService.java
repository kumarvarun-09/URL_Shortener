package com.example.URL.Shortener.rateLimiter;

public interface IRateLimiterService {
 boolean isAllowed(String key);
}
