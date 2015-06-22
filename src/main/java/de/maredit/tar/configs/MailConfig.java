package de.maredit.tar.configs;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Profile("smtpMailService")
@Configuration
public class MailConfig {

  @Bean
  public MailProperties mailProperties() {
    return new MailProperties();
  }

  @Bean
  public JavaMailSender mailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    MailProperties properties = mailProperties();
    mailSender.setHost(properties.getHost());
    if (properties.getPort() != null) {
      mailSender.setPort(properties.getPort());
    }
    mailSender.setUsername(properties.getUsername());
    mailSender.setPassword(properties.getPassword());
    return mailSender;
  }
}
