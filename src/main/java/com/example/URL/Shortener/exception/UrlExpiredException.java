package com.example.URL.Shortener.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class UrlExpiredException extends RuntimeException {
    private final String shortCode;
    public UrlExpiredException(String message, String shortCode) {
        super(message);
        this.shortCode = shortCode;
        log.info("URL for shortUrl {} has been expired", message);
    }
}
