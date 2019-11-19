package com.pulsar.consumer.subscriber;

import lombok.SneakyThrows;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;

import static java.util.Objects.nonNull;

public abstract class PulsarSubscriber implements Runnable {

    public abstract Consumer consumer();
    public abstract void process(Message record);

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
                consumer().negativeAcknowledge(msg);
            }
        }
    }

    //ensure consumer connection can be established
    public void connect() {
        Consumer consumer = consumer();

        if (nonNull(consumer) && consumer.isConnected()) {
            // start listening for messages
            Thread thread = new Thread(this);
            thread.setName(consumerId() + "-" + thread.getName());
            thread.start();
        } else {
            throw new IllegalStateException("Unable to connect to pulsar");
        }
    }

    // can be overridden in subscriber class
    public Boolean shouldBeStarted() {
        return true;
    }

    // can be overridden in subscriber class
    public String consumerId() {
        return this.getClass().getSimpleName();
    }

}
