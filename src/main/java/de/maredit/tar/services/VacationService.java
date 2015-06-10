package de.maredit.tar.services;

import de.maredit.tar.models.LastingVacation;

import de.maredit.tar.models.enums.State;
import de.maredit.tar.models.UserVacationAccount;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import de.maredit.tar.models.Vacation;

public class VacationService {

  public double getCountOfVacation(Vacation vacation) {
    LocalDate startDate = vacation.getFrom();
    LocalDate endDate = vacation.getTo();
    double result = 0;
    if (startDate.equals(endDate)) {
      result = vacation.getDays() < 1 ? 0.5 : 1;
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
      if (!isWeekEnd(startDate)) {
        result++;
      }
    } while (!startDate.equals(endDate));
    return result;
  }

  public boolean isWeekEnd(LocalDate date) {
    return date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY;
  }

  public LastingVacation getLastingVacationDays(UserVacationAccount account) {
    LastingVacation result = new LastingVacation(30, 5);
    LocalDate remainingDate = LocalDate.now().withMonth(4).withDayOfMonth(1);
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
      if (vacation.getFrom().isBefore(remainingDate)) {
        if (vacation.getTo().isBefore(remainingDate)) {
          if (result.getVacationDaysLastYear() > 0) {
            if (vacation.getDays() > result.getVacationDaysLastYear()) {
              double days = vacation.getDays() - result.getVacationDaysLastYear();
              result.reduceVacationDaysLastYear(result.getVacationDaysLastYear());
              result.reduceVacationDays(days);
            } else {
              result.reduceVacationDaysLastYear(vacation.getDays());
            }
          } else {
            result.reduceVacationDays(vacation.getDays());
          }
        } else {
          double daysBefore = calculateDays(vacation.getFrom(), remainingDate.minusDays(1));
          double daysAfter = calculateDays(remainingDate, vacation.getTo());

          if (daysBefore > result.getVacationDaysLastYear()) {
            double days = daysBefore - result.getVacationDaysLastYear();
            result.reduceVacationDaysLastYear(result.getVacationDaysLastYear());
            result.reduceVacationDays(days);
          } else {
            result.reduceVacationDaysLastYear(daysBefore);
          }
          result.reduceVacationDays(daysAfter);

        }
      } else {
        result.reduceVacationDays(vacation.getDays());
      }
    }
    return result;
  }
}
