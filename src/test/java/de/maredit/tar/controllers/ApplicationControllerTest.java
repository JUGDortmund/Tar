package de.maredit.tar.controllers;

import de.maredit.tar.Main;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class ApplicationControllerTest {

  @Autowired
  private Environment env;

  @Test
  public void foo() {
    System.out.println("---> " + env.getProperty("mongodb.port"));
  }
}
