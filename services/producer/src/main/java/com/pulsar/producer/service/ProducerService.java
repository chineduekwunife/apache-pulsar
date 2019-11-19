package com.pulsar.producer.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Service;

/**
 * A service for sending messages to Pulsar Broker.
 *
 * @author Chinedu Ekwunife
 */
@Service
@RequiredArgsConstructor
public class ProducerService {

    private final PulsarClient pulsarClient;

    @SneakyThrows
    public <T> void sendMessage(T message, String topic, Schema<T> schema) {
        try (Producer<T> producer = getProducer(topic, schema)) {
            producer.send(message);
        }
    }

    @SneakyThrows
    private <T> Producer<T> getProducer(String topic, Schema<T> schema) {
        return pulsarClient.newProducer(schema)
                .topic(topic)
                .create();
    }
}
