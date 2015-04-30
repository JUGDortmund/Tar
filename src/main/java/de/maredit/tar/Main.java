package de.maredit.tar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.listeners.StartupListener;

@SpringBootApplication
@EnableScheduling
public class Main {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(Main.class);
    springApplication.addListeners(new StartupListener(), new ContextListener());
    springApplication.run(args);
  }
}