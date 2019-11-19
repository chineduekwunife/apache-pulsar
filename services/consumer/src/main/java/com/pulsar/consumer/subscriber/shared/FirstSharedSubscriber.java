package com.pulsar.consumer.subscriber.shared;

import com.pulsar.consumer.subscriber.PulsarSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.SubscriptionType;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Subscription.SHARED_SUBSCRIPTION;

/**
 * Multiple consumer will be able to use the same subscription name and the messages will be dispatched according to
 * a round-robin rotation between the connected consumers.
 * <p>
 * In this mode, the consumption order is not guaranteed.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirstSharedSubscriber extends PulsarSubscriber {

    private final PulsarClient pulsarClient;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            consumer = pulsarClient.newConsumer()
                    .topic(MY_TOPIC)
                    .subscriptionName(SHARED_SUBSCRIPTION)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscribe();
        }

        return consumer;
    }

    @Override
    public void process(Message msg) {
        log.info("Message received: {}", new String(msg.getData()));
    }
}
