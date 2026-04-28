package com.example.URL.Shortener.service;

public interface IRateLimiterService {
 boolean isAllowed(String key);
}
