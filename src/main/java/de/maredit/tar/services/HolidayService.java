package de.maredit.tar.services;

import java.util.Set;

public interface HolidayService {

  /**
   * Get the list of all holidays (nrw)
   */
  Set<de.jollyday.Holiday> getAllHolidays(int year);
}
