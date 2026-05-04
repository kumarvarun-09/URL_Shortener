package com.example.URL.Shortener.service;

public interface IAnalyticsService {
    void incrementClick(String shortCode);
    Long getClicks(String shortCode);
}
