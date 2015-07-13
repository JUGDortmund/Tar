package de.maredit.tar.services;

import de.maredit.tar.data.CommentItem;
import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.User;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.Holiday;
import de.maredit.tar.models.VacationEntitlement;
import de.maredit.tar.models.enums.ManualEntryType;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.CommentItemRepository;
import de.maredit.tar.repositories.VacationRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VacationService {

  @Autowired
  private HolidayService holidayService;

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private CommentItemRepository commentItemRepository;

  public double getValueOfVacation(Vacation vacation) {
    LocalDate startDate = vacation.getFrom();
    LocalDate endDate = vacation.getTo();
    double result = 0;

    // wenn start und enddatum gleich sind, kann es nur ein einzelner oder ein halber Urlaubstag sein
    if (startDate.equals(endDate)) {
      if (!isWeekEnd(startDate)) {

        Holiday
            holiday =
            getHoliday(startDate, holidayService.getHolidayPeriodOfTime(startDate, startDate));
        if (holiday != null) {
          result = 1 - holiday.getValence();
        } else {
          result = vacation.isHalfDay() ? 0.5 : 1;
        }
      }
    } else {
      result = calculateDays(startDate, endDate);
    }
    if (vacation.getManualEntry() != null){
      result = result - vacation.getManualEntry().getDays();
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

  private Holiday getHoliday(LocalDate date, Set<Holiday> holidays) {
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


  /**
   * Helper method to retrieve the amount of approved vacation days for a list of vacations.
   *
   * @param set the set to analyze
   * @return the amount of approved vacation days
   */
  public double getApprovedVacationDays(Set<Vacation> set) {
    return set != null ? set.stream().filter(vacation -> vacation.getState() == State.APPROVED)
        .mapToDouble(vacation -> {
          if (vacation.getManualEntry() != null) {
            return vacation.getDays() - vacation.getManualEntry().getDays();
          } else {
            return vacation.getDays();
          }
        }).sum() : 0;
  }

  /**
   * Helper method to retrieve the amount of pending vacation days (which are already planned but
   * not accepted yet) for a list of vacations.
   *
   * @param set the set to analyze
   * @return the amount of pending vacation days
   */
  public double getPendingVacationDays(Set<Vacation> set) {
    return set != null ? set
        .stream()
        .filter(
            vacation -> vacation.getState() == State.REQUESTED_SUBSTITUTE
                        || vacation.getState() == State.WAITING_FOR_APPROVEMENT)
        .mapToDouble(vacation -> vacation.getDays()).sum() : 0;
  }


  public VacationEntitlement getRemainingVacationEntitlement(UserVacationAccount account) {
    VacationEntitlement
        result =
        new VacationEntitlement(account.getTotalVacationDays(),
                                account.getPreviousYearOpenVacationDays());
    LocalDate expiryDate = account.getExpiryDate();

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
      double valueOfVacation = getValueOfVacation(vacation);
      if (vacation.getFrom().isBefore(expiryDate)) {
        if (vacation.getTo().isBefore(expiryDate)) {

          // Zuerst Resturlaub aufbrauchen
          if (result.getDaysLastYear() > 0) {
            if (valueOfVacation > result.getDaysLastYear()) {
              double days = valueOfVacation - result.getDaysLastYear();
              result.reduceDaysLastYear(result.getDaysLastYear());
              result.reduceDays(days);
            } else {
              result.reduceDaysLastYear(valueOfVacation);
            }
          } else {
            result.reduceDays(valueOfVacation);
          }
        } else {

          // Ein Teil des Urlaubs kann mit Resturlaub bedient werden
          double daysBefore = calculateDays(vacation.getFrom(), expiryDate.minusDays(1));
          double daysAfter = calculateDays(expiryDate, vacation.getTo());

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
        result.reduceDays(valueOfVacation);
      }
    }

    // Filtern und sortieren der Eingangsdaten
    List<ManualEntry> manualEntries =
        account
            .getManualEntries()
            .stream()
            .filter(
                manualEntry -> manualEntry.getVacation() == null)
            .sorted((manualEntry1, manualEntry2) -> manualEntry1.getCreated().compareTo(
                manualEntry2.getCreated()))
            .collect(Collectors.toList());

    for (ManualEntry manualEntry : manualEntries) {
      if (manualEntry.getType() == ManualEntryType.ADD){
        result.reduceDays(-manualEntry.getDays());
      } else {
        result.reduceDays(manualEntry.getDays());
      }
    }
    return result;
  }

  public List<Vacation> getSubstituteVacationsForUser(User user) {
    List<Vacation> substitutes =
        vacationRepository.findVacationBySubstituteAndStateNotAndToAfterOrderByFromAsc(
            user, State.CANCELED, LocalDate.now().minusDays(1));
    return substitutes;
  }

  public List<Vacation> getVacationsForUser(User user) {
    List<Vacation> vacations =
        vacationRepository.findVacationByUserAndStateNotOrderByFromAsc(user, State.CANCELED);
    return vacations;
  }

  public List<Vacation> getVacationsForApprovalForUser(User user) {
    List<Vacation> approvals =
        vacationRepository.findVacationByManagerAndState(user, State.WAITING_FOR_APPROVEMENT);
    return approvals;
  }

  public List<Vacation> getVacationsForSubstituteApprovalForUser(User user) {
    List<Vacation> substitutesForApproval =
        vacationRepository.findVacationBySubstituteAndState(user, State.REQUESTED_SUBSTITUTE);
    return substitutesForApproval;
  }

  public CommentItem saveComment(String comment, Vacation vacation, User author) {
    if (StringUtils.isNotBlank(comment)) {
      CommentItem commentItem = new CommentItem();
      commentItem.setModifed(LocalDateTime.now());
      commentItem.setCreated(LocalDateTime.now());
      commentItem.setText(comment);
      commentItem.setAuthor(author);
      commentItem.setVacation(vacation);
      commentItemRepository.save(commentItem);
      return commentItem;
    }
    return null;
  }
}
