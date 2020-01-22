package com.pulsar.config;

import com.pulsar.admin.PulsarAdminService;
import com.pulsar.model.PulsarSubscriberExecutor;
import com.pulsar.model.PulsarSubscriberThreadCounter;
import lombok.SneakyThrows;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class PulsarConsumerConfig {

    @Bean
    @SneakyThrows
    public PulsarClient pulsarClient(@Value("${pulsar.service.url}") String serviceUrl) {
        return PulsarClient.builder()
                .serviceUrl(serviceUrl)
                .build();
    }

    @Bean
    @SneakyThrows
    public PulsarAdmin pulsarAdmin(@Value("${pulsar.admin.url}") String serviceUrl) {
        return PulsarAdmin.builder()
                .serviceHttpUrl(serviceUrl)
                .build();
    }

    @Bean
    public PulsarAdminService pulsarAdminService(PulsarAdmin pulsarAdmin, @Value("${kubernetes.namespace}") String kubeNamespace) {
        return new PulsarAdminService(pulsarAdmin, kubeNamespace);
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
    public PulsarSubscriberExecutor pulsarSubscriberExecutor() {
        ThreadGroup threadGroup = new ThreadGroup("pulsar-subscribers");

        ExecutorService executor = Executors.newCachedThreadPool(r -> new Thread(threadGroup, r));

        return new PulsarSubscriberExecutor(threadGroup, executor);
    }

    @Bean
    public CommandLineRunner subscriberThreadCounter(PulsarSubscriberExecutor pulsarSubscriberExecutor) {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setThreadNamePrefix("pulsar-sub-counter");
        return args -> executor.execute(new PulsarSubscriberThreadCounter(pulsarSubscriberExecutor));
    }
}
