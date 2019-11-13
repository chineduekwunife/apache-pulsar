package com.novafutur.api.message.akka.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import com.novafutur.api.message.akka.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.novafutur.api.message.akka.messages.SupervisorMessages.GetAsyncActorRef;


/**
 * Supervisor actor creates child actors and handles exceptions which occurs in any of its children
 *
 * @author Chinedu Ekwunife
 */
@Component
@Scope("prototype")
public class Supervisor extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final ActorRef async;

    @Autowired
    public Supervisor(SpringExtension springExtension) {
        async = context().actorOf(springExtension.props("async"), "async");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetAsyncActorRef.class, message -> getSender().tell(async, getSelf()))
                .matchAny(o -> logger.info("received unknown message"))
                .build();
    }

    //handling exceptions
    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(10, Duration.create(1, TimeUnit.MINUTES), DeciderBuilder.
                match(ActorInitializationException.class, e -> {
                    logger.info("Child actor failed to initialise");
                    return SupervisorStrategy.stop();
                }).
                match(InvalidMessageException.class, e -> {
                    logger.info("Child actor received invalid message");
                    return SupervisorStrategy.resume();
                }).
                matchAny(o -> {
                    logger.error("Received unhandled {} from child actor with message {}", o.getClass(), o.getMessage());
                    return SupervisorStrategy.resume();
                })
                .build());
    }
}
