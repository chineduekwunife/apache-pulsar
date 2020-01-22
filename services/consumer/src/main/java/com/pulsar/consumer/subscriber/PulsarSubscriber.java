package com.pulsar.consumer.subscriber;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

import static java.util.Objects.nonNull;

/**
 * Generic class to be extended by all subscribers to Apache Pulsar.
 *
 * @author Chinedu Ekwunife
 */
public abstract class PulsarSubscriber<T> implements Runnable {

    private Logger log = LoggerFactory.getLogger(PulsarSubscriber.class);

    private Future<?> future;

    public abstract Consumer<T> consumer();

    public abstract void process(Message record);

    @Override
    public void run() {
        runRoutine();
    }

    private void runRoutine() {
        while (consumerIsConnected()) {
            // Wait for a message
            Message msg = null;
            try {
                msg = consumer().receive();
            } catch (PulsarClientException e) {
                logReceiveException(e);
            }

            if (nonNull(msg)) {
                try {
                    // Do something with the message
                    process(msg);

                    // Acknowledge the message so that it can be deleted by the message broker
                    consumer().acknowledge(msg);
                } catch (PulsarClientException e) {
                    acknowledgeRoutine(msg, e);
                } catch (Exception ex) {
                    // Message failed during processing
                    log.error("Exception occurred while processing message", ex);

                    // Acknowledge the message so that it can be deleted by the message broker
                    try {
                        consumer().acknowledge(msg);
                    } catch (PulsarClientException e) {
                        acknowledgeRoutine(msg, e);
                    }
                }
            }
        }
    }

    /**
     * @param future returned after runnable is submitted to executor service
     */
    public void start(Future future) {
        this.future = future;
    }

    // can be overridden in subscriber class
    public Boolean shouldBeStarted() {
        return true;
    }

    // can be overridden in subscriber class
    public String consumerId() {
        return this.getClass().getSimpleName();
    }

    /**
     * close Pulsar subscriber and also terminate underlying thread.
     */
    public void close() {
        try {
            this.consumer().close();
        } catch (PulsarClientException e) {
            log.error("Exception occurred while closing consumer", e);
        }

        if (nonNull(this.future)) {
            this.future.cancel(true);
        }
    }

    private boolean consumerIsConnected() {
        return nonNull(consumer()) && consumer().isConnected();
    }

    private void logReceiveException(PulsarClientException e) {
        //InterruptedException will be thrown when subscriber thread is closed, so no need to log
        if (!(e.getCause() instanceof InterruptedException)) {
            log.error("Exception occurred while waiting to receive message:", e);
        }
    }

    private void acknowledgeRoutine(Message msg, PulsarClientException e) {
        // Message failed during acknowledge, redeliver later
        log.error("Exception occurred while acknowledging message", e);

        if (consumer().isConnected()) {
            consumer().negativeAcknowledge(msg);
        }

    }
}
