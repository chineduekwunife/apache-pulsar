package com.pulsar.consumer.subscriber.schema;

import com.pulsar.consumer.subscriber.PulsarSubscriber;
import com.pulsar.model.SensorReading;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.pulsar.client.impl.schema.JSONSchema;
import org.springframework.stereotype.Component;

import static com.pulsar.model.constants.Subscription.EXCLUSIVE_SUBSCRIPTION;
import static com.pulsar.model.constants.Topic.SENSOR_TOPIC;
import static java.util.Objects.isNull;

/**
 * Demonstrates a subscriber for custom java object, in this case an object of type SensorReading
 *
 * @author Chinedu Ekwunife
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SensorReadingSubscriber extends PulsarSubscriber {

    private final PulsarClient pulsarClient;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            consumer = pulsarClient.newConsumer(JSONSchema.of(SensorReading.class))
                    .topic(SENSOR_TOPIC)
                    .subscriptionName(EXCLUSIVE_SUBSCRIPTION)
                    .subscriptionType(SubscriptionType.Exclusive)
                    .subscribe();
        }

        return consumer;
    }

    @Override
    public void process(Message msg) {
        log.info("Sensor reading received: {}", ((SensorReading) msg.getValue()).getTemperature());
    }
}
