package com.pulsar.consumer.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public abstract class PulsarSubscriber implements Runnable {

    public abstract Consumer consumer();

    public abstract void process(Message record);

    public abstract Boolean shouldBeStarted();

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void run() {
        while (true) {
            // Wait for a message
            Message msg = consumer().receive();

            try {
                // Do something with the message
                process(msg);

                // Acknowledge the message so that it can be deleted by the message broker
                consumer().acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                log.error("Unexpected error: {}", e);
                consumer().negativeAcknowledge(msg);
            }
        }
    }

    public void connect() {
        //ensure consumer connection can be established
        Consumer consumer = consumer();

        if (nonNull(consumer) && consumer.isConnected()) {
            // start listening for messages
            new Thread(this).start();
        } else {
            throw new IllegalStateException("Unable to connect to pulsar, retrying...");
        }
    }

}
