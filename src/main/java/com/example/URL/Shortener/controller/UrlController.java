package com.example.URL.Shortener.controller;

import com.example.URL.Shortener.exception.UrlExpiredException;
import com.example.URL.Shortener.exception.UrlNotFoundException;
import com.example.URL.Shortener.request.ShortenUrlRequest;
import com.example.URL.Shortener.response.ApiResponse;
import com.example.URL.Shortener.service.IUrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;

@Slf4j
@RestController
@RequestMapping("/url")
@RequiredArgsConstructor
public class UrlController {
    private final IUrlService urlService;

    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody ShortenUrlRequest request) {
        try {
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
    public ResponseEntity<?> getOriginalUrl(@PathVariable(name = "shortCode") String shortCode) {
        try {
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
