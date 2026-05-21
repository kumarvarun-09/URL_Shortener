package com.example.URL.Shortener.controller;

import com.example.URL.Shortener.rateLimiter.IRateLimiterService;
import com.example.URL.Shortener.request.ShortenUrlRequest;
import com.example.URL.Shortener.response.ApiResponse;
import com.example.URL.Shortener.service.IUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final IUrlService urlService;
    private final IRateLimiterService rateLimiterService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@Valid @RequestBody ShortenUrlRequest request) {
        String originalUrl = request.getUrl().trim();
        String shortCode = urlService.shortenUrl(originalUrl);
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        true,
                        "Short URL Created",
                        shortCode,
                        LocalDateTime.now())
                );
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable(name = "shortCode") String shortCode) {
        shortCode = shortCode.trim();
        String originalUrl = urlService.getOriginalUrl(shortCode);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse("Original URL found", originalUrl));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }
}
