package de.maredit.tar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoDataAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude={MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}