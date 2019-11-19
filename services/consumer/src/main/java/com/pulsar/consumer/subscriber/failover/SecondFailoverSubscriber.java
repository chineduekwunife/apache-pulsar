package com.pulsar.consumer.subscriber.failover;

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
import static com.pulsar.consumer.constants.Topic.MY_TOPIC;
import static com.pulsar.consumer.constants.Subscription.FAILOVER_SUBSCRIPTION;

/**
 * Multiple consumer will be able to use the same subscription name but only 1 consumer will receive the messages.
 * If that consumer disconnects, one of the other connected consumers will start receiving messages.
 * <p>
 * In failover mode, the consumption ordering is guaranteed.
 * <p>
 * In case of partitioned topics, the ordering is guaranteed on a per-partition basis. The partitions assignments will
 * be split across the available consumers. On each partition, at most one consumer will be active at a given point
 * in time.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecondFailoverSubscriber extends PulsarSubscriber {

    private final PulsarClient pulsarClient;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            consumer = pulsarClient.newConsumer()
                    .topic(MY_TOPIC)
                    .subscriptionName(FAILOVER_SUBSCRIPTION)
                    .subscriptionType(SubscriptionType.Failover)
                    .subscribe();
        }

        return consumer;
    }

    @Override
    public void process(Message msg) {
        log.info("Message received: {}", new String(msg.getData()));
    }
}
