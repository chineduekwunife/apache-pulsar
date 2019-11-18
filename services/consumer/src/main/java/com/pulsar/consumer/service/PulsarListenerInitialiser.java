package com.pulsar.consumer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;

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

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        subscribers.stream()
                .filter(PulsarSubscriber::shouldBeStarted)
                .forEach(pulsarSubscriber -> retryTemplate.execute(context -> {
                    pulsarSubscriber.connect();

                    return true;
                }));
    }
}
