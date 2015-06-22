package de.maredit.tar.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties {
  
  private String sender;
  
  private String prefix;

  private String[] additionalRecipients;

  private String urlToVacation;

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

  public String getUrlToVacation() {
    return urlToVacation;
  }

  public void setUrlToVacation(String urlToVacation) {
    this.urlToVacation = urlToVacation;
  }
}
