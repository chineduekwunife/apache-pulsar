package com.pulsar.model;

import java.util.concurrent.ExecutorService;

@SuppressWarnings({"squid:S3014"})
public class PulsarSubscriberExecutor {

    private final ThreadGroup threadGroup;
    private final ExecutorService executorService;

    public PulsarSubscriberExecutor(ThreadGroup threadGroup, ExecutorService executorService) {
        this.threadGroup = threadGroup;
        this.executorService = executorService;
    }

    public ThreadGroup getThreadGroup() {
        return threadGroup;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
