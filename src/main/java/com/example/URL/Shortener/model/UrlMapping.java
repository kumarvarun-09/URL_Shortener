package com.example.URL.Shortener.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "url_mapping",
        indexes = {
                @Index(name = "idx_short_code", columnList = "shortUrl", unique = true),
                @Index(name = "idx_expiry", columnList = "expiryAt")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UrlMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String originalUrl;
    @Column(nullable = false, unique = true, length = 10)
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiryAt;
}
