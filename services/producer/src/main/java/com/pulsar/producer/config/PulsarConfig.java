package com.pulsar.producer.config;

import lombok.SneakyThrows;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Apache Pulsar.
 *
 * @author Chinedu Ekwunife
 */
@Configuration
public class PulsarConfig {

    @Bean
    @SneakyThrows
    public PulsarClient pulsarClient(@Value("${pulsar.serviceUrl}") String serviceUrl) {
        return PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();
    }
}
