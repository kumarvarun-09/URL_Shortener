package com.example.URL.Shortener.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UrlNotFoundException extends ResourceNotFoundException {
    private final String shortCode;

    public UrlNotFoundException(String message, String shortUrl) {
        super(message);
        this.shortCode = shortUrl;
        log.info("URL for shortUrl {} not found", shortUrl);
    }
}
