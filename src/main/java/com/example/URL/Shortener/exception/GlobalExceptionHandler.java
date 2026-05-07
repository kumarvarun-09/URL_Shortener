package com.example.URL.Shortener.exception;

import com.example.URL.Shortener.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult()
                .getFieldError()
                .getDefaultMessage();
        log.warn("Validation failed: {}", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(
                        false,
                        errorMessage,
                        null,
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<?> handleUrlNotFoundException(UrlNotFoundException e) {
        log.warn("URL not found for shortCode: {}", e.getShortCode());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(
                        false,
                        e.getMessage(),
                        e.getShortCode(),
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<?> handleUrlExpiredException(UrlExpiredException e) {
        log.error("URL expired exception occurred: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.GONE)
                .body(new ApiResponse(
                        false,
                        e.getMessage(),
                        e.getShortCode(),
                        LocalDateTime.now())
                );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                                false,
                                "Internal server error",
                                null,
                                LocalDateTime.now()
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        log.error("Unexpected exception occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(
                        false,
                        "Internal server error",
                        null,
                        LocalDateTime.now())
                );
    }
}
