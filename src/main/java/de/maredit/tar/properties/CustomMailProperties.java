package de.maredit.tar.properties;


import org.springframework.stereotype.Component;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties {

  private String[] additionalRecipients;

  public String[] getAdditionalRecipients() {
    return additionalRecipients;
  }

  public void setAdditionalRecipients(String[] additionalRecipients) {
    this.additionalRecipients = additionalRecipients;
  }


}
