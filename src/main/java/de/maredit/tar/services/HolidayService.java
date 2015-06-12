package de.maredit.tar.services;

import java.time.LocalDate;
import java.util.Set;

import de.maredit.tar.models.UserHoliday;

public interface HolidayService {

  /**
   * Get the list of all holidays (nrw)
   */
  Set<UserHoliday> getAllHolidays(int year);
  
  /**
   * Get the list of all holiday in a period of time
   */
  Set<UserHoliday> getHolidayPeriodOfTime(LocalDate startDate, LocalDate endDate, int year);
}
