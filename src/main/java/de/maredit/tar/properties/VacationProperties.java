package de.maredit.tar.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(locations = "classpath:vacationConfig.yaml")
public class VacationProperties {

  private int defaultVacationDays;

  public int getDefaultVacationDays() {
    return defaultVacationDays;
  }

  public void setDefaultVacationDays(int defaultVacationDays) {
    this.defaultVacationDays = defaultVacationDays;
  }
}
