package com.pulsar.consumer.subscriber;

import lombok.SneakyThrows;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;

/**
 * Generic class to be extended by all subscribers to Apache Pulsar.
 *
 * @author Chinedu Ekwunife
 */
public abstract class PulsarSubscriber implements Runnable {

    public abstract Consumer consumer();

    public abstract void process(Message record);

    @SneakyThrows
    public void run() {
        consumerRoutine();
    }

    private void consumerRoutine() throws PulsarClientException {
        // Wait for a message
        Message msg = consumer().receive();

        try {
            // Do something with the message
            process(msg);

            // Acknowledge the message so that it can be deleted by the message broker
            consumer().acknowledge(msg);
        } catch (Exception e) {
            // Message failed to process, redeliver later
            consumer().negativeAcknowledge(msg);
        }

        consumerRoutine();
    }

    // can be overridden in subscriber class
    public Boolean shouldBeStarted() {
        return true;
    }

}
