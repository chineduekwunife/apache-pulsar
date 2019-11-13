package com.novafutur.api.message.akka;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;

/**
 *  Producer for actors using Spring
 *
 *  @author Chinedu Ekwunife
 */
@RequiredArgsConstructor
public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;

    @Override
    public Actor produce() {
        return applicationContext.getBean(actorBeanName, Actor.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}
