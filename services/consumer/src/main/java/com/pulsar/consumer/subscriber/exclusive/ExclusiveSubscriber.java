package com.pulsar.consumer.subscriber.exclusive;

import com.pulsar.consumer.subscriber.PulsarSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Subscription.EXCLUSIVE_SUBSCRIPTION;


/**
 * There can be only 1 consumer on the same topic with the same subscription name.
 *
 * @author Chinedu Ekwunife
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExclusiveSubscriber extends PulsarSubscriber {

    private final PulsarClient pulsarClient;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            consumer = pulsarClient.newConsumer()
                    .topic(MY_TOPIC)
                    .subscriptionName(EXCLUSIVE_SUBSCRIPTION)
                    .subscriptionType(SubscriptionType.Exclusive)
                    .subscribe();
        }

        return consumer;
    }

    @Override
    public void process(Message msg) {
        log.info("Message received: {}", new String(msg.getData()));
    }

    @Override
    public Boolean shouldBeStarted() {
        return false;
    }

}
