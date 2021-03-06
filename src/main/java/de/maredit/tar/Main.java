package de.maredit.tar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.maredit.tar.listeners.ContextListener;
import de.maredit.tar.listeners.StartupListener;


@SpringBootApplication
@EnableScheduling
@EnableCaching
public class Main {
  @SuppressWarnings("unused")
  private static final Logger LOG = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(Main.class);
    springApplication.addListeners(new StartupListener(), new ContextListener());
    springApplication.run(args);
  }
}
