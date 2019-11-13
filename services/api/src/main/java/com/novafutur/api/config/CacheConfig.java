package com.novafutur.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class CacheConfig {

    @Bean
    public ConcurrentHashMap<Number, Number> cache() {
        return new ConcurrentHashMap<>();
    }
}
