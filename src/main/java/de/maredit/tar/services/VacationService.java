package de.maredit.tar.services;

import de.maredit.tar.models.Holiday;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.State;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VacationService {
  
  @Autowired
  private HolidayService holidayService;

  public double getCountOfVacation(Vacation vacation) {
    LocalDate startDate = vacation.getFrom();
    LocalDate endDate = vacation.getTo();
    double result = 0;
    
    // wenn start und enddatum gleich sind, kann es nur ein einzelner oder ein halber Urlaubstag sein
    if (startDate.equals(endDate)) {
      if (!isWeekEnd(startDate)) {
        
        Holiday holiday = getHoliday(startDate, holidayService.getHolidayPeriodOfTime(startDate, startDate));
        if (holiday != null) {
          result = 1 - holiday.getValence();
        } else {
          result = vacation.isHalfDay() ? 0.5 : 1;
        }
      }
    } else {
      result = calculateDays(startDate, endDate);
    }
    return result;
  }

  private double calculateDays(LocalDate startDate, LocalDate endDate) {
    double result;
    result = 0;
    Set<Holiday> holidays = holidayService.getHolidayPeriodOfTime(startDate, endDate);
    LocalDate date = startDate;
    do {
      if (!isWeekEnd(date)) {
        
        Holiday holiday = getHoliday(date, holidays);
        if (holiday == null) {
          result++;
        } else {
          result += (1 - holiday.getValence());
        }
      }
      date = date.plusDays(1);
    } while (!date.isAfter(endDate));
    return result;
  }
  
  private Holiday getHoliday(LocalDate date, Set<Holiday> holidays)  {
    for (Holiday holiday : holidays) {
      if (holiday.getDate().equals(date)) {
        return holiday;
      }
    }
    return null;
  }

  public boolean isWeekEnd(LocalDate date) {
    return date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY;
  }

  public VacationEntitlement getRemainingVacationDays(UserVacationAccount account) {
    VacationEntitlement result = new VacationEntitlement(account.getTotalVacationDays(), account .getPreviousYearOpenVacationDays() == null ? 0 : account.getPreviousYearOpenVacationDays());
    LocalDate expiaryDate = account.getExpiryDate();
    
    // Filtern und sortieren der Eingangsdaten
    List<Vacation> vacations =
        account
            .getVacations()
            .stream()
            .filter(
                vacation -> vacation.getState() != State.REJECTED
                    && vacation.getState() != State.CANCELED)
            .sorted((vaction1, vacation2) -> vaction1.getFrom().compareTo(vacation2.getFrom()))
            .collect(Collectors.toList());
    
    for (Vacation vacation : vacations) {
      double countOfVacation = getCountOfVacation(vacation);
      if (vacation.getFrom().isBefore(expiaryDate)) {
        if (vacation.getTo().isBefore(expiaryDate)) {
          
          // Zuerst Resturlaub aufbrauchen
          if (result.getDaysLastYear() > 0) {
            if (countOfVacation > result.getDaysLastYear()) {
              double days = countOfVacation - result.getDaysLastYear();
              result.reduceDaysLastYear(result.getDaysLastYear());
              result.reduceDays(days);
            } else {
              result.reduceDaysLastYear(countOfVacation);
            }
          } else {
            result.reduceDays(countOfVacation);
          }
        } else {
          
          // Ein Teil des Urlaubs kann mit Resturlaub bedient werden
          double daysBefore = calculateDays(vacation.getFrom(), expiaryDate.minusDays(1));
          double daysAfter = calculateDays(expiaryDate, vacation.getTo());

          if (daysBefore > result.getDaysLastYear()) {
            double days = daysBefore - result.getDaysLastYear();
            result.reduceDaysLastYear(result.getDaysLastYear());
            result.reduceDays(days);
          } else {
            result.reduceDaysLastYear(daysBefore);
          }
          result.reduceDays(daysAfter);

        }
      } else {
        
        // normaler Abzug
        result.reduceDays(countOfVacation);
      }
    }
    return result;
  }
}
