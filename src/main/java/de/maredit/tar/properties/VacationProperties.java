package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(locations = "classpath:vacationConfig.yaml")
public class VacationProperties {

  private double defaultVacationDays;

  public double getDefaultVacationDays() {
    return defaultVacationDays;
  }

  public void setDefaultVacationDays(double defaultVacationDays) {
    this.defaultVacationDays = defaultVacationDays;
  }
}
