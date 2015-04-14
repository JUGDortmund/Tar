package de.maredit.tar;

import javax.annotation.PreDestroy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        EmbeddedMongo.DB.port(29019).start();
        SpringApplication.run(Main.class, args);
    }
    
    @PreDestroy
    public void shutdown() {
        EmbeddedMongo.DB.stop();
    }
}