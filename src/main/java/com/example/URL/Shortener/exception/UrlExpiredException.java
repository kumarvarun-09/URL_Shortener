package com.example.URL.Shortener.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String message) {
        super(message);
        log.info("URL for shortUrl {} has been expired", message);
    }
}
