package com.pulsar.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PulsarSubscriberThreadCounter implements Runnable {
    private Logger log = LoggerFactory.getLogger(PulsarSubscriberThreadCounter.class);

    private final PulsarSubscriberExecutor pulsarSubscriberExecutor;

    public PulsarSubscriberThreadCounter(PulsarSubscriberExecutor pulsarSubscriberExecutor) {
        this.pulsarSubscriberExecutor = pulsarSubscriberExecutor;
    }

    @Override
    public void run() {
        while (!pulsarSubscriberExecutor.getExecutorService().isShutdown()) {
            try {
                Thread.sleep(30000); // Wait 30 seconds
            } catch (InterruptedException e) {
                log.warn("Thread interrupted due to: {}", e.getMessage());
                Thread.currentThread().interrupt();
            }
            log.info("Active pulsar subscriber count: [{}]", pulsarSubscriberExecutor.getThreadGroup().activeCount());
        }
    }
}
