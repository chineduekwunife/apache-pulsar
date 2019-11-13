package com.novafutur.api.message.akka.messages;

import java.io.Serializable;

/**
 *  Messages sent to Supervisor actor
 *
 *  @author Chinedu Ekwunife
 */
public interface SupervisorMessages {

    class GetAsyncActorRef implements Serializable {}
}
