package com.pulsar.consumer.subscriber.shared;

import com.pulsar.admin.PulsarAdminService;
import com.pulsar.consumer.subscriber.PulsarSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;
import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Subscription.SHARED_SUBSCRIPTION;

/**
 * Multiple consumer will be able to use the same subscription name and the messages will be dispatched according to
 * a round-robin rotation between the connected consumers.
 * <p>
 * In this mode, the consumption order is not guaranteed.
 *
 * @author Chinedu Ekwunife
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FirstSharedSubscriber extends PulsarSubscriber {

    private static final String APP = "sample-app";

    private final PulsarClient pulsarClient;
    private final PulsarAdminService pulsarAdminService;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            String pulsarTopic = pulsarAdminService.topic(MY_TOPIC, APP);
            consumer = pulsarClient.newConsumer()
                    .topic(pulsarTopic)
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



    @Override
    public Boolean shouldBeStarted() {
        return true;
    }

}
