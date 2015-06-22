package de.maredit.tar.properties;


import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import de.maredit.tar.models.UserHoliday;

@Component
@ConfigurationProperties(locations = "classpath:vacationConfig.yaml")
public class VacationProperties {

  private double defaultVacationDays;
  private String expiryDate;
  private List<UserHoliday> userHolidays;

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

  public List<UserHoliday> getUserHolidays() {
    return userHolidays;
  }

  public void setUserHolidays(List<UserHoliday> userHolidays) {
    this.userHolidays = userHolidays;
  }
}
