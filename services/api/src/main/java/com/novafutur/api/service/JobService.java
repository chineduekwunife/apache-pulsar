package com.novafutur.api.service;

import akka.actor.ActorRef;
import com.novafutur.api.message.akka.actor.ActorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import static akka.pattern.Patterns.ask;
import static akka.actor.ActorRef.noSender;
import static java.util.Objects.isNull;
import static com.novafutur.api.message.akka.messages.SupervisorMessages.GetAsyncActorRef;
import static com.novafutur.api.message.akka.messages.AsyncMessages.Execute;

/**
 * A service for returning the compute value from cache or sending the computation to an actor to be done async.
 *
 * @author Chinedu Ekwunife
 */
@Service
public class JobService {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    private static final Duration timeout = Duration.of(ActorConstants.TIMEOUT, ChronoUnit.SECONDS);

    private static final String DEFAULT_MESSAGE = "we’re calculating the answer, please come back in an unpredictable time...";

    private final ActorRef async;
    private final ConcurrentHashMap<Number, Number> cache;
    private final CollatzService collatzService;

    @Autowired
    public JobService(@Qualifier("supervisorActor") ActorRef supervisor, ConcurrentHashMap<Number, Number> cache, CollatzService collatzService) {
        this.async = (ActorRef) ask(supervisor, new GetAsyncActorRef(), timeout).toCompletableFuture().join();
        this.cache = cache;
        this.collatzService = collatzService;
    }

    public Object compute(Integer number) {
        if (isNull(number) || !(number > 0)) {
            throw new InvalidParameterException("Number is invalid, only positive numbers are allowed");
        }

        if (cache.containsKey(number)) {
            logger.info("Result for {} exists in cache", number);

            return cache.get(number);
        }

        //send message to async actor
        logger.warn("Result for {} does not exist in cache, calculating asynchronously", number);

        async.tell(new Execute(o -> computeAsync(number)), noSender());

        //return default message
        return DEFAULT_MESSAGE;
    }

    private void computeAsync(Integer number) {
        //Compute the function in the background and store result in cache
        Integer iterations = collatzService.computeCollatz(number);

        cache.putIfAbsent(number, iterations);
    }
}
