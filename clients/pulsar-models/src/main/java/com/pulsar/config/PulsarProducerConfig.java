package com.pulsar.config;

import com.pulsar.admin.PulsarAdminService;
import lombok.SneakyThrows;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PulsarProducerConfig {

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
}
