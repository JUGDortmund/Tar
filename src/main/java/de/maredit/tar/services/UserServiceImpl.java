package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by czillmann on 19.05.15.
 */
@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VacationRepository vacationRepository;

  @Override
  public List<User> getSortedUserList() {
    List<User> userList = userRepository.findAll();
    userList =
        userList
            .stream()
            .filter(e -> e.isActive())
            .sorted(
                (e1, e2) -> e1.getLastname().toLowerCase()
                    .compareTo(e2.getLastname().toLowerCase())).collect(Collectors.toList());
    return userList;
  }

  @Override
  public UserVacationAccount getUserVacationAccountForYear(User user, int year) {
    UserVacationAccount vacationAccount = new UserVacationAccount();
    List<Vacation> vacations = getVacationsForUserAndYear(user, year);
    List<Vacation> previousVacations = getVacationsForUserAndYear(user, year - 1);

    vacationAccount.setUser(user);
    vacationAccount.setVacations(vacations);
    vacationAccount.setApprovedVacationDays(getApprovedVacationDays(vacations));
    vacationAccount.setPendingVacationDays(getPendingVacationDays(vacations));
    vacationAccount.setOpenVacationDays(getOpenVacationDays(vacations));
    vacationAccount
        .setPreviousYearOpenVacationDays(getPreviousYearOpenVacationDays(previousVacations));

    return vacationAccount;
  }

  @Override
  public List<UserVacationAccount> getUserVacationAccountsForYear(List<User> users, int year) {
    List<UserVacationAccount> vacationAccounts = new ArrayList<UserVacationAccount>();
    for (User user : users) {
      vacationAccounts.add(getUserVacationAccountForYear(user, year));
    }
    return vacationAccounts;
  }

  @Override
  public List<Vacation> getVacationsForUserAndYear(User user, int year) {
    LocalDate startOfYear = LocalDate.now().withYear(year).with(
        TemporalAdjusters.firstDayOfYear()).minusDays(1);
    LocalDate endOfYear = LocalDate.now().withYear(year).with(
        TemporalAdjusters.lastDayOfYear()).plusDays(1);

    return this.vacationRepository.findVacationByUserAndFromBetweenOrUserAndToBetween(
        user, startOfYear, endOfYear, user, startOfYear, endOfYear);
  }

  /**
   * Helper method to retrieve the amount of approved vacation days for a list of vacations.
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic calculation of vacation is
   * done!
   *
   * @param vacations the list to analyze
   * @return the amount of approved vacation days
   */
  private double getApprovedVacationDays(List<Vacation> vacations) {
    return vacations.stream().filter(vacation -> vacation.getState() == State.APPROVED)
        .mapToDouble(vacation -> vacation.getDays()).sum();
  }

  /**
   * Helper method to retrieve the amount of pending vacation days (which are already planned but
   * not accepted yet) for a list of vacations.
   * !!!!! WARNING: Those methods do have to be changed,
   * as soon as automatic calculation of vacation is done!
   *
   * @param vacations the list to analyze
   * @return the amount of pending vacation days
   */
  private double getPendingVacationDays(List<Vacation> vacations) {
    return vacations.stream().filter(vacation -> vacation.getState() == State.REQUESTED_SUBSTITUTE
                                                 || vacation.getState()
                                                    == State.WAITING_FOR_APPROVEMENT)
        .mapToDouble(vacation -> vacation.getDays()).sum();
  }

  /**
   * Helper method to retrieve the amount of open vacation days (which can still be planned) for a
   * list of vacations.
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic
   * calculation of vacation is done!
   *
   * @param vacations the list to analyze
   * @return the amount of open vacation days
   */
  private double getOpenVacationDays(List<Vacation> vacations) {
    Optional result =
        vacations.stream()
            .filter(vacation -> vacation.getCreated().getYear() == LocalDate.now().getYear())
            .filter(vacation -> vacation.getState() != State.CANCELED
                                && vacation.getState() != State.REJECTED)
            .max((v1, v2) -> v1.getCreated().compareTo(v2.getCreated()));

    return result.isPresent() ? ((Vacation) result.get()).getDaysLeft() : 0;
  }

  /**
   * Helper method to retrieve the amount of open vacation days (which can still be planned) for a
   * list of vacations.
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic
   * calculation of vacation is done!
   *
   * @param vacations the list to analyze
   * @return the amount of open vacation days
   */
  private double getPreviousYearOpenVacationDays(List<Vacation> vacations) {
    Optional result =
        vacations.stream()
            .filter(vacation -> vacation.getState() != State.CANCELED
                                && vacation.getState() != State.REJECTED)
            .max((v1, v2) -> v1.getCreated().compareTo(v2.getCreated()));

    return result.isPresent() ? ((Vacation) result.get()).getDaysLeft() : 0;
  }
}