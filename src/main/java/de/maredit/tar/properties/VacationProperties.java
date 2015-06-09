package de.maredit.tar.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import de.maredit.tar.models.UserHoliday;

@Component
@ConfigurationProperties(locations = "classpath:vacationConfig.yaml")
public class VacationProperties {

  private int defaultVacationDays;
  private List<UserHoliday> userHolidays;

  public int getDefaultVacationDays() {
    return defaultVacationDays;
  }

  public void setDefaultVacationDays(int defaultVacationDays) {
    this.defaultVacationDays = defaultVacationDays;
  }

  public List<UserHoliday> getUserHolidays() {
    return userHolidays;
  }

  public void setUserHolidays(List<UserHoliday> userHolidays) {
    this.userHolidays = userHolidays;
  }
}
