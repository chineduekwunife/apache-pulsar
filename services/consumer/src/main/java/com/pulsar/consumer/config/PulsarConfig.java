package com.pulsar.consumer.config;

import lombok.SneakyThrows;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration for Apache Pulsar.
 *
 * @author Chinedu Ekwunife
 */
@Configuration
public class PulsarConfig {

    @Bean
    @SneakyThrows
    public PulsarClient pulsarClient(@Value("${pulsar.service.url}") String serviceUrl) {
        return PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();
    }

    @Bean
    public RetryTemplate retryTemplate(@Value("${pulsar.retry.backoff.period}") int pulsarRetryBackOffPeriod, @Value("${pulsar.retry.max.attempts}") int pulsarRetryMaxAttempts) {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(pulsarRetryBackOffPeriod);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(pulsarRetryMaxAttempts);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newCachedThreadPool();
    }
}
