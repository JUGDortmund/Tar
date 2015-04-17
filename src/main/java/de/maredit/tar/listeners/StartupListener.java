package de.maredit.tar.listeners;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

public class StartupListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Boolean embeddedMongo = environment.getProperty("spring.data.mongodb.embedded", Boolean.class);
        if (embeddedMongo != null && embeddedMongo.booleanValue()) {
            EmbeddedMongo.DB.port(environment.getProperty("spring.data.mongodb.port", Integer.class)).start();
        }
    }
}