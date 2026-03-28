package com.example.URL.Shortener.service;

public interface IUrlService {
    String shortenUrl(String originalUrl);

    String getOriginalUrl(String shortenUrl);
}
