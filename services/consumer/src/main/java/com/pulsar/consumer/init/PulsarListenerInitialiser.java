package com.pulsar.consumer.init;

import com.pulsar.consumer.subscriber.PulsarSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * Application Listener used for initializing Pulsar consumers after application startup
 *
 * @author Chinedu Ekwunife
 */
@Component
@ConditionalOnProperty(prefix = "message", name = "bus.connect", havingValue = "true")
@RequiredArgsConstructor
public class PulsarListenerInitialiser implements ApplicationListener<ApplicationReadyEvent> {

    private final Collection<PulsarSubscriber> subscribers;
    private final RetryTemplate retryTemplate;
    private final ExecutorService executorService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribers.stream()
                .filter(PulsarSubscriber::shouldBeStarted)
                .forEach(pulsarSubscriber -> retryTemplate.execute(context -> {
                    executorService.submit(pulsarSubscriber);

                    return true;
                }));
    }
}
