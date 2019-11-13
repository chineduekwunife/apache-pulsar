package com.novafutur.api.message.akka.actor;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import akka.routing.DefaultResizer;
import akka.routing.RoundRobinPool;
import com.novafutur.api.message.akka.SpringExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

import static com.novafutur.api.message.akka.messages.AsyncMessages.Execute;


/**
 * An actor for executing operations asynchronously.
 * For async consumer functions, Async actor will hand the function over to a pool of Async Worker actors, which executes the function in a round-robbin order.
 *
 * @author Chinedu Ekwunife
 */
@Component
@Scope("prototype")
public class Async extends AbstractActor {

    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);
    private static final int LOWER_BOUND = ActorConstants.ASYNC_LOWER_BOUND;
    private static final int UPPER_BOUND = ActorConstants.ASYNC_UPPER_BOUND;

    private final ActorRef asyncWorkerPool;

    @Autowired
    public Async(SpringExtension springExtension) {
        this.asyncWorkerPool = context().actorOf(new RoundRobinPool(LOWER_BOUND).withResizer(new DefaultResizer(LOWER_BOUND, UPPER_BOUND))
                .withSupervisorStrategy(strategy())
                .props(springExtension.props("asyncWorker")), "asyncWorker");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Execute.class, msg -> asyncWorkerPool.tell(msg, getSelf()))
                .matchAny(o -> logger.info("received unknown message"))
                .build();
    }

    private SupervisorStrategy strategy() {
        return new OneForOneStrategy(5, Duration.create(1, TimeUnit.MINUTES), DeciderBuilder.
                match(ActorInitializationException.class, e -> {
                    logger.info("Child actor failed to initialise");
                    return SupervisorStrategy.stop();
                }).
                matchAny(o -> {
                    logger.error("Received unhandled {} from child actor with message {}", o.getClass(), o.getMessage());
                    return SupervisorStrategy.resume();
                })
                .build());
    }
}
