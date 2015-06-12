package de.maredit.tar.services;

import de.maredit.tar.models.VacationEntitlement;

import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class VacationService {

  public double getCountOfVacation(Vacation vacation) {
    LocalDate startDate = vacation.getFrom();
    LocalDate endDate = vacation.getTo();
    double result = 0;
    
    // wenn start und enddatum gleich sind, kann es nur ein einzelner oder ein halber Urlaubstag sein
    if (startDate.equals(endDate)) {
      if (!isWeekEnd(startDate)) {
        
        if (isHoliday(startDate)) {
          // "Sondertage" wie Silvester und Heiligabend zählen als halbe Tage
        } else {
          // handling für halbe Tage
          result = vacation.getDays() < 1 ? 0.5 : 1;
        }
      }
    } else {
      result = calculateDays(startDate, endDate);
    }
    return result;
  }

  private double calculateDays(LocalDate startDate, LocalDate endDate) {
    double result;
    result = 1;
    do {
      startDate = startDate.plusDays(1);
      if (isHoliday(startDate)) {
        // "Sondertage" wie Silvester und Heiligabend zählen als halbe Tage
      } else if (!isWeekEnd(startDate)) {
        result++;
      }
    } while (!startDate.equals(endDate));
    return result;
  }
  
  public boolean isHoliday(LocalDate date)  {
    // TODO hier Prüfung auf Feiertage
    return false;
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
