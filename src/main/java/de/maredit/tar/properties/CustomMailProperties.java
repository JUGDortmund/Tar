package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
public class CustomMailProperties {

  private String[] additionalRecipients;

  private String urlToVacation;

  public String[] getAdditionalRecipients() {
    return additionalRecipients;
  }

  public void setAdditionalRecipients(String[] additionalRecipients) {
    this.additionalRecipients = additionalRecipients;
  }

  public String getUrlToVacation() {
    return urlToVacation;
  }

  public void setUrlToVacation(String urlToVacation) {
    this.urlToVacation = urlToVacation;
  }

}
