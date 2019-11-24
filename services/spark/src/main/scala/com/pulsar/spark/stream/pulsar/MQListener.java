package com.pulsar.spark.stream.pulsar;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener for Apache Pulsar messages.
 *
 * @author Chinedu Ekwunife
 */
public class MQListener<T> implements MessageListener<T> {
    private Logger log = LoggerFactory.getLogger(MQListener.class);

    @Override
    public void received(Consumer<T> consumer, Message<T> msg) {
        try {
            // Do something with the message
            log.info(">>>>>>>>>>>>>>>>>>>>>>>> Message processed by consumer {}", new String(msg.getData()));

            // Acknowledge the message so that it can be deleted by the message broker
            consumer.acknowledge(msg);
        } catch (Exception e) {
            // Message failed to process, redeliver later
            consumer.negativeAcknowledge(msg);
        }
    }
}
