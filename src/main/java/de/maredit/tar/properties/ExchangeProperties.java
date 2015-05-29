package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

/**
 * Created by czillmann on 24.04.15.
 */

@Component
@ConfigurationProperties(prefix = "spring.exchange")
public class ExchangeProperties {

  private URI service;
  private String user;
  private String password;
  private String calendar;

  public URI getService() {
    return service;
  }

  public void setService(URI service) {
    this.service = service;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getCalendar() {
    return calendar;
  }

  public void setCalendar(String calendar) {
    this.calendar = calendar;
  }
}
