package com.example.URL.Shortener.controller;

import com.example.URL.Shortener.exception.UrlExpiredException;
import com.example.URL.Shortener.exception.UrlNotFoundException;
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

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final IUrlService urlService;
    private final IRateLimiterService rateLimiterService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(HttpServletRequest httpServletRequest, @RequestBody ShortenUrlRequest request) {
        try {
            final String ip = httpServletRequest.getRemoteAddr();
            log.info("Got request from IP: {} " +
                            "\n user: {} " +
                            "\n host: {} " +
                            "\n port: {} ", ip, httpServletRequest.getRemoteUser(),
                    httpServletRequest.getRemoteHost(),
                    httpServletRequest.getRemotePort());
            if (!rateLimiterService.isAllowed(ip)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Too many requests");
            }
            String originalUrl = request.getUrl().trim();
            String shortCode = urlService.shortenUrl(originalUrl);
            return ResponseEntity.ok().body(new ApiResponse("ShortUrlCreated", shortCode));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), request.getUrl()));
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> getOriginalUrl(HttpServletRequest httpServletRequest , @PathVariable(name = "shortCode") String shortCode) {
        try {
            final String ip = httpServletRequest.getRemoteAddr();
            log.info("Got request from IP: {} " +
                            "\n user: {} " +
                            "\n host: {} " +
                            "\n port: {} ", ip, httpServletRequest.getRemoteUser(),
                    httpServletRequest.getRemoteHost(),
                    httpServletRequest.getRemotePort());
            if (!rateLimiterService.isAllowed(ip)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Too many requests");
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
        } catch (UrlNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(e.getMessage(), shortCode));
        } catch (UrlExpiredException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), shortCode));
        }
    }


}
