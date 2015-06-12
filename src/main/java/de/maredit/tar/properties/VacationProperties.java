package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(locations = "classpath:vacationConfig.yaml")
public class VacationProperties {

  private double defaultVacationDays;
  
  private String expiryDate;

  public double getDefaultVacationDays() {
    return defaultVacationDays;
  }

  public void setDefaultVacationDays(double defaultVacationDays) {
    this.defaultVacationDays = defaultVacationDays;
  }

  public String getExpiryDate() {
    return expiryDate;
  }

  public void setExpiryDate(String expiryDate) {
    this.expiryDate = expiryDate;
  }
}
