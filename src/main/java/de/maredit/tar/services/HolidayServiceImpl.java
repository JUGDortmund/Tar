package de.maredit.tar.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameter;
import de.jollyday.ManagerParameters;
import de.maredit.tar.models.UserHoliday;
import de.maredit.tar.properties.VacationProperties;

/**
 * Created by phorninge on 05.06.2015
 */
@Service
public class HolidayServiceImpl implements HolidayService {

  @Autowired
  private VacationProperties properties;

  private static final Logger LOG = LogManager.getLogger(HolidayServiceImpl.class);
  private ManagerParameter parameters = ManagerParameters.create(HolidayCalendar.GERMANY);
  private List<UserHoliday> userHolidays = new ArrayList<UserHoliday>();
  private Set<Holiday> extHolidays;
  private Set<UserHoliday> allHolidays = new HashSet<UserHoliday>();
  private Set<UserHoliday> periodHolidays = new HashSet<UserHoliday>();

  @Override
  public Set<UserHoliday> getAllHolidays(int year) {
    HolidayManager m = HolidayManager.getInstance(parameters);
    extHolidays = m.getHolidays(year, "nw");
    userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      allHolidays.add(addUserHoliday(h));
    }
    allHolidays.addAll(userHolidays);

    return allHolidays;
  }

  @Override
  public Set<UserHoliday> getHolidayPeriodOfTime(LocalDate startDate, LocalDate endDate, int year) {

    periodHolidays.clear();
    HolidayManager m = HolidayManager.getInstance(parameters);
    extHolidays = m.getHolidays(startDate, endDate, "nw");
    userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      periodHolidays.add(addUserHoliday(h));
    }

    for (UserHoliday uH : userHolidays) {
      if (!uH.getDate().startsWith(year + "")) {
        String[] parts = uH.getDate().split("-");
        String monthSplit = parts[1];
        String daySplit = parts[2];

        uH.setDate(year + "-" + monthSplit + "-" + daySplit);
      }

      if (!startDate.isBefore(startDate) && !endDate.isAfter(endDate)) {
        periodHolidays.add(uH);
      }
    }

    return periodHolidays;
  }


  private UserHoliday addUserHoliday(de.jollyday.Holiday holiday) {
    UserHoliday uHoliday = new UserHoliday();
    uHoliday.setDate(holiday.getDate().toString());
    uHoliday.setValence(1);
    uHoliday.setDescription(holiday.getDescription());

    return uHoliday;
  }
}
