package com.example.URL.Shortener.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlNotFoundException extends ResourceNotFoundException {
    public UrlNotFoundException(String shortUrl) {
        super(shortUrl);
        log.info("URL for shortUrl {} not found", shortUrl);
    }
}
