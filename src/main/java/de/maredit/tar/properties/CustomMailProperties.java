package de.maredit.tar.properties;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties extends MailProperties {

  List<String> additionalrecipients;

  public List<String> getAdditionalrecipients() {
    return additionalrecipients;
  }

  public void setAdditionalrecipients(List<String> additionalrecipients) {
    this.additionalrecipients = additionalrecipients;
  }


}
