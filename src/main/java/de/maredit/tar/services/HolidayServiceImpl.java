package de.maredit.tar.services;

import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import de.jollyday.ManagerParameter;
import de.jollyday.ManagerParameters;
import de.maredit.tar.models.Holiday;
import de.maredit.tar.models.UserHoliday;
import de.maredit.tar.properties.VacationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by phorninge on 05.06.2015
 */
@Service
public class HolidayServiceImpl implements HolidayService {

  @Autowired
  private VacationProperties properties;

  private static final Logger LOG = LogManager.getLogger(HolidayServiceImpl.class);
  private static final ManagerParameter
      parameters =
      ManagerParameters.create(HolidayCalendar.GERMANY);
  private static final DateTimeFormatter
      dateTimeFormatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Override
  public Set<Holiday> getAllHolidays(int year) {
    HolidayManager m = HolidayManager.getInstance(parameters);
    Set<de.jollyday.Holiday> extHolidays = m.getHolidays(year, "nw");
    Set<Holiday> allHolidays = new HashSet<>();
    List<UserHoliday> userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      allHolidays.add(new Holiday(h.getDate(), h.getDescription(LocaleContextHolder.getLocale())));
    }
    for (UserHoliday uH : userHolidays) {
      Holiday userHoliday = new Holiday(LocalDate.parse(year + "-" + uH.getDate(), dateTimeFormatter), uH.getDescription());
      userHoliday.setValence(uH.getValence());
      allHolidays.add(userHoliday);
    }

    return allHolidays;
  }

  @Override
  public Set<Holiday> getHolidayPeriodOfTime(LocalDate startDate, LocalDate endDate) {

    Set<Holiday> periodHolidays = new HashSet<>();
    HolidayManager m = HolidayManager.getInstance(parameters);
    Set<de.jollyday.Holiday> extHolidays = m.getHolidays(startDate, endDate, "nw");
    List<UserHoliday> userHolidays = properties.getUserHolidays();

    for (de.jollyday.Holiday h : extHolidays) {
      periodHolidays.add(new Holiday(h.getDate(), h.getDescription(LocaleContextHolder.getLocale())));
    }

    int startYear = startDate.getYear();
    int endYear = endDate.getYear();
    for (; startYear <= endYear; startYear++) {
      for (UserHoliday uH : userHolidays) {
        Holiday userHoliday = new Holiday(LocalDate.parse(startYear + "-" + uH.getDate(), dateTimeFormatter), uH.getDescription());
        userHoliday.setValence(uH.getValence());
        if (startDate.isBefore(userHoliday.getDate()) && endDate.isAfter(userHoliday.getDate())) {
          periodHolidays.add(userHoliday);
        }
      }
    }

    return periodHolidays;
  }


  private UserHoliday addUserHoliday(Holiday holiday) {
    UserHoliday uHoliday = new UserHoliday();
    uHoliday.setDate(holiday.getDate().toString());
    uHoliday.setValence(1);
    uHoliday.setDescription(holiday.getDescription());

    return uHoliday;
  }
}
