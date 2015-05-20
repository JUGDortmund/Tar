package de.maredit.tar.properties;


import org.springframework.stereotype.Component;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Component
@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties {
  
  private String sender;
  
  private String prefix;

  private String[] additionalRecipients;

  public String[] getAdditionalRecipients() {
    return additionalRecipients;
  }

  public void setAdditionalRecipients(String[] additionalRecipients) {
    this.additionalRecipients = additionalRecipients;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
}
