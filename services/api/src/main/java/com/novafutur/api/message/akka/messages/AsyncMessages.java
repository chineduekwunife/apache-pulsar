package com.novafutur.api.message.akka.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Messages sent to Async actor.
 *
 * @author Chinedu Ekwunife
 */
public interface AsyncMessages {

    @RequiredArgsConstructor
    class Execute implements Serializable {

        @Getter
        private final Consumer<Object> consumer;

    }
}
