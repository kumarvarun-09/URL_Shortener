package com.example.URL.Shortener.controller;

import com.example.URL.Shortener.request.ShortenUrlRequest;
import com.example.URL.Shortener.response.ApiResponse;
import com.example.URL.Shortener.service.IRateLimiterService;
import com.example.URL.Shortener.service.IUrlService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> shortenUrl(HttpServletRequest httpServletRequest, @RequestBody ShortenUrlRequest request) {
        final String ip = httpServletRequest.getRemoteAddr();
        log.info("""
                Got request from IP: {}
                """, ip);
        if (!rateLimiterService.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse(
                            false,
                            "Too many requests",
                            null, LocalDateTime.now())
                    );
        }
        String originalUrl = request.getUrl().trim();
        String shortCode = urlService.shortenUrl(originalUrl);
        return ResponseEntity.ok()
                .body(new ApiResponse(
                        true,
                        "ShortUrlCreated",
                        shortCode,
                        LocalDateTime.now())
                );
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> getOriginalUrl(HttpServletRequest httpServletRequest, @PathVariable(name = "shortCode") String shortCode) {
        final String ip = httpServletRequest.getRemoteAddr();
        log.info("""
                Got request from IP: {}
                """, ip);
        if (!rateLimiterService.isAllowed(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new ApiResponse(
                            false,
                            "Too many requests",
                            null, LocalDateTime.now())
                    );
        }
        shortCode = shortCode.trim();
        String originalUrl = urlService.getOriginalUrl(shortCode);
//            return ResponseEntity.ok()
//                    .body(new ApiResponse("Original URL found", originalUrl));
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
        /*   🧠 Interview-Level Insight
            If asked: “How do you implement redirect?”
            You say: “I return HTTP 302 with Location header. Browser handles the redirection.”
            Bonus:
            302 → temporary redirect
            301 → permanent redirect
        */
    }
}
