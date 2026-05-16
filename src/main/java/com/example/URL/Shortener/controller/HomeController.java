package com.example.URL.Shortener.controller;

import com.example.URL.Shortener.config.ApplicationProperties;
import com.example.URL.Shortener.response.ApiResponse;
import com.example.URL.Shortener.service.IRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HomeController {
    private final IRateLimiterService rateLimiterService;
    private final ApplicationProperties applicationProperties;

    @GetMapping("/")
    public ResponseEntity<?> home(HttpServletRequest httpServletRequest) {
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

        Map<String, Object> response = getStringObjectMap();
        return ResponseEntity.ok(response);
    }

    private @NonNull Map<String, Object> getStringObjectMap() {
        String baseUrl = applicationProperties.getBaseUrl().replace("/url/", "");
        Map<String, Object> response = new HashMap<>();

        response.put("service", "URL Shortener API");
        response.put("status", "running");
        response.put("description", "Production-grade URL shortening service with caching and analytics");

        Map<String, String> links = new HashMap<>();
        links.put("swagger", baseUrl + "/swagger-ui/index.html");
        links.put("health", baseUrl + "/actuator/health");

        response.put("links", links);
        return response;
    }
}