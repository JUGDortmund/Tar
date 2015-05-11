package de.maredit.tar.properties;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties extends MailProperties {

  private String[] additionalRecipients;

  public String[] getAdditionalRecipients() {
    return additionalRecipients;
  }

  public void setAdditionalRecipients(String[] additionalRecipients) {
    this.additionalRecipients = additionalRecipients;
  }


}
