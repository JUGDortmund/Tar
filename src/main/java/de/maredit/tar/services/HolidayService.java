package de.maredit.tar.services;

import de.maredit.tar.models.Holiday;

import java.time.LocalDate;
import java.util.Set;

public interface HolidayService {

  /**
   * Get the list of all holidays (nrw)
   */
  Set<Holiday> getAllHolidays(int year);
  
  /**
   * Get the list of all holiday in a period of time
   */
  Set<Holiday> getHolidayPeriodOfTime(LocalDate startDate, LocalDate endDate);
}
