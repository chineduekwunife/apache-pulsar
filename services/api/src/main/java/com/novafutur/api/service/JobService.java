package com.novafutur.api.service;

import akka.actor.ActorRef;
import com.novafutur.api.message.akka.actor.ActorConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import static akka.pattern.Patterns.ask;
import static java.util.Objects.isNull;
import static com.novafutur.api.message.akka.messages.SupervisorMessages.GetAsyncActorRef;
import static com.novafutur.api.message.akka.messages.AsyncMessages.Execute;

/**
 * A service for returning the compute value from cache or sending the computation to an actor to be done async and Python code.
 *
 * @author Chinedu Ekwunife
 */
@Service
public class JobService {

    private final Logger logger = LoggerFactory.getLogger(JobService.class);

    private static final Duration timeout = Duration.of(ActorConstants.TIMEOUT, ChronoUnit.SECONDS);

    private static final String DEFAULT_MESSAGE = "we’re calculating the answer, please come back in an unpredictable time...";

    private final ConcurrentHashMap<Number, Number> cache;
    private final ActorRef async;

    @Autowired
    public JobService(@Qualifier("supervisorActor") ActorRef supervisor, ConcurrentHashMap<Number, Number> cache) {
        this.async = (ActorRef) ask(supervisor, new GetAsyncActorRef(), timeout).toCompletableFuture().join();
        this.cache = cache;
    }

    public Object compute(BigDecimal number) {
        if (isNull(number) || number.signum() < 0) {
            throw new InvalidParameterException("Number is invalid, only positive numbers are allowed");
        }

        if (cache.containsKey(number)) {
            return cache.get(number);
        }

        //send message to async actor
        async.tell(new Execute(o -> computeAsync(number)), ActorRef.noSender());

        //return default message
        return DEFAULT_MESSAGE;
    }

    private void computeAsync(Number number) {
        //TODO compute the function in the background and store result in cache

        Number result = 1; //TODO
        cache.putIfAbsent(number, result);
    }
}
