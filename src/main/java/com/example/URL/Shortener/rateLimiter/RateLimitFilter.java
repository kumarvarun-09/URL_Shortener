package com.example.URL.Shortener.rateLimiter;

import com.example.URL.Shortener.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final IRateLimiterService rateLimiterService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String clientIp = getClientIP(request);
        log.info("""
                Got request from IP: {}
                """, clientIp);
        if (!rateLimiterService.isAllowed(clientIp)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter()
                    .write(new ObjectMapper().
                            writeValueAsString(
                                    new ApiResponse(
                                            false,
                                            "Too many requests",
                                            null,
                                            LocalDateTime.now()
                                    )
                            )
                    );
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/")
                || path.startsWith("swagger-ui")
                || path.startsWith("v3/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/favicon.ico");
    }

    private String getClientIP(HttpServletRequest request) {
        String forwardedHeader = request.getHeader("X-Forwarded-For");
        if (forwardedHeader != null && !forwardedHeader.isBlank()) {
            return forwardedHeader.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
