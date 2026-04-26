package com.example.URL.Shortener.service;

public interface IRedisService {
    <T> T get(String key, Class<T> entityClass);
    void set(String key, Object value, Long ttl);
    void remove(String key);
}
