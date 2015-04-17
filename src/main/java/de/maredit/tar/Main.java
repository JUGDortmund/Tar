package de.maredit.tar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;

@SpringBootApplication
@EnableScheduling
public class Main {
    public static void main(String[] args) {
        if (!("prod").equalsIgnoreCase(System.getProperty("spring.profiles.active"))) {
            EmbeddedMongo.DB.port(28018).start();
        }
        
        SpringApplication.run(Main.class, args);
    }
}