package com.example.URL.Shortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//Swagger / OpenAPI behavior
@Configuration
@Slf4j
public class OpenApiConfig {

    @Value("${app.base-url}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("baseUrl");
        return new OpenAPI()
                .servers(List.of(
                        new Server().url(baseUrl.replace("/url/", ""))
                ));
    }
}

