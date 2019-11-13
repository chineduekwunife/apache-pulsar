package com.novafutur.api.message.akka.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.novafutur.api.message.akka.messages.AsyncMessages.Execute;


/**
 * A worker actor for executing asynchronous functions.
 *
 * @author Chinedu Ekwunife
 */
@Component
@Scope("prototype")
public class AsyncWorker extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Execute.class, msg -> msg.getConsumer().accept(""))
                .matchAny(o -> logger.info("received unknown message"))
                .build();
    }
}
