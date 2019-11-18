package com.pulsar.producer.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
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
    public void sendMessage() {
        try (Producer<byte[]> producer = getProducer()) {
            // You can then send messages to the broker and topic you specified:
            producer.send("My message".getBytes());
        }
    }

    @SneakyThrows
    private Producer<byte[]> getProducer() {
        return pulsarClient.newProducer()
                .topic("my-topic")
                .create();
    }
}
