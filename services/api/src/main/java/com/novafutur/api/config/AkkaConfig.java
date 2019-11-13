package com.novafutur.api.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.novafutur.api.message.akka.SpringExtension;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Novafutur akka system
 *
 * @author Chinedu Ekwunife
 */
@Configuration
@RequiredArgsConstructor
public class AkkaConfig {

    private final ApplicationContext applicationContext;
    private final SpringExtension springExtension;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem system = ActorSystem.create("novafutur", akkaConfiguration());
        springExtension.initialize(applicationContext);
        return system;
    }

    @Bean
    public Config akkaConfiguration() {
        return ConfigFactory.load();
    }

    @Bean(name = "supervisorActor")
    public ActorRef supervisorActor(ActorSystem actorSystem) {
        return actorSystem.actorOf(springExtension.props("supervisor"), "Supervisor");
    }
}
