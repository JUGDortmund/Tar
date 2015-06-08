package de.maredit.tar.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameter;
import de.jollyday.ManagerParameters;

@Service
public class HolidayServiceImpl implements HolidayService {

  private static final Logger LOG = LogManager.getLogger(HolidayServiceImpl.class);
  private ManagerParameter parameters = ManagerParameters.create(HolidayCalendar.GERMANY);
  private Set<de.jollyday.Holiday> mareditHolidays;
  private Set<de.jollyday.Holiday> extHolidays;
  private Set<de.jollyday.Holiday> allHolidays;

  @Override
  public Set<de.jollyday.Holiday> getAllHolidays(int year) {
    HolidayManager m = HolidayManager.getInstance(parameters);
    extHolidays = m.getHolidays(year, "nw");

//    try {
//      mareditHolidays = getMareditHolidays(year);
//    } catch (MalformedURLException e) {
//      // TODO Auto-generated catch block
//      e.printStackTrace();
//    }

    for (de.jollyday.Holiday h : extHolidays) {
      LOG.debug("Holiday: {}", h.getDate());
      allHolidays.add(h);
    }

    for (de.jollyday.Holiday mH : allHolidays) {
      LOG.debug("All Maredit Holiday: {}", mH.getDate());
    }

    return extHolidays;
  }

  private Set<de.jollyday.Holiday> getMareditHolidays(int year) throws MalformedURLException {
    URL url = new URL("**resources/maredit-holidays.xml");
    ManagerParameter parameters = ManagerParameters.create(url);
    HolidayManager manager = HolidayManager.getInstance(parameters);

    Set<de.jollyday.Holiday> holidays = manager.getHolidays(year);

    return holidays;
  }
}
