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

  @Override
  public Set<UserHoliday> getAllHolidays(int year) {
    HolidayManager m = HolidayManager.getInstance(parameters);
    Set<Holiday> extHolidays = m.getHolidays(year, "nw");
    Set<UserHoliday> allHolidays = new HashSet<>();
    List<UserHoliday> userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      allHolidays.add(addUserHoliday(h));
    }
    for (UserHoliday uH : userHolidays) {
      UserHoliday userHoliday = new UserHoliday(uH);
      userHoliday.setDate(year + "-" + uH.getDate());
      allHolidays.add(userHoliday);
    }

    return allHolidays;
  }

  @Override
  public Set<UserHoliday> getHolidayPeriodOfTime(LocalDate startDate, LocalDate endDate, int year) {

    Set<UserHoliday> periodHolidays = new HashSet<UserHoliday>();
    HolidayManager m = HolidayManager.getInstance(parameters);
    Set<Holiday> extHolidays = m.getHolidays(startDate, endDate, "nw");
    List<UserHoliday> userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      periodHolidays.add(addUserHoliday(h));
    }

    for (UserHoliday uH : userHolidays) {
      UserHoliday userHoliday = new UserHoliday(uH);
      userHoliday.setDate(year + "-" + uH.getDate());
      if (!startDate.isBefore(startDate) && !endDate.isAfter(endDate)) {
        periodHolidays.add(userHoliday);
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
