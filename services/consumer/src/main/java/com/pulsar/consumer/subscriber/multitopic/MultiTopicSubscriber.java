package com.pulsar.consumer.subscriber.multitopic;

import com.pulsar.consumer.subscriber.PulsarSubscriber;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.stereotype.Component;

import static com.pulsar.model.constants.Topic.MY_TOPIC;
import static com.pulsar.model.constants.Topic.MY_SECOND_TOPIC;
import static com.pulsar.model.constants.Subscription.MULTI_TOPIC_SUBSCRIPTION;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;


/**
 * Can subscribe to different topics.
 *
 * @author Chinedu Ekwunife
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MultiTopicSubscriber extends PulsarSubscriber {

    private final PulsarClient pulsarClient;

    private Consumer consumer;

    @Override
    @SneakyThrows
    public Consumer consumer() {
        if (isNull(consumer)) {
            consumer = pulsarClient.newConsumer()
                    .subscriptionName(MULTI_TOPIC_SUBSCRIPTION)
                    .topics(
                            asList(
                                    MY_TOPIC,
                                    MY_SECOND_TOPIC
                            )
                    )
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
