package com.example.URL.Shortener.repository;

import com.example.URL.Shortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);
    Optional<UrlMapping> findTopByOrderByIdDesc();
}
