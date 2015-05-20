package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserAccount;
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
    List<User> userList = new ArrayList<User>();
    userList = userRepository.findAll();
    userList =
        userList
            .stream()
            .filter(e -> e.isActive())
            .sorted(
                (e1, e2) -> e1.getLastname().toUpperCase()
                    .compareTo(e2.getLastname().toUpperCase())).collect(Collectors.toList());
    return userList;
  }

  @Override
  public UserAccount getUserAccountForYear(User user, int year) {
    UserAccount account = new UserAccount();
    List<Vacation> vacations = getVacationsForUserAndYear(user, year);
    List<Vacation> previousVacations = getVacationsForUserAndYear(user, year - 1);

    account.setUser(user);
    account.setVacations(vacations);
    account.setApprovedVacationDays(getApprovedVacationDays(vacations));
    account.setPendingVacationDays(getPendingVacationDays(vacations));
    account.setOpenVacationDays(getOpenVacationDays(vacations));
    account.setPreviousYearOpenVacationDays(getPreviousYearOpenVacationDays(previousVacations));

    return account;
  }

  @Override
  public List<UserAccount> getUserAccountsForYear(List<User> users, int year) {
    List<UserAccount> accounts = new ArrayList<UserAccount>();
    for (User user : users) {
      accounts.add(getUserAccountForYear(user, year));
    }
    return accounts;
  }

  @Override
  public List<Vacation> getVacationsForUserAndYear(User user, int year) {
    LocalDate startOfYear = LocalDate.ofYearDay(year, 1).with(TemporalAdjusters.firstDayOfYear());
    LocalDate endOfYear = LocalDate.ofYearDay(year, 1).with(TemporalAdjusters.lastDayOfYear());

    return this.vacationRepository.findVacationByUserAndFromBetweenOrUserAndToBetween(
        user, startOfYear, endOfYear, user, startOfYear, endOfYear);
  }

  /**
   * Helper method to retrieve the amount of approved vacation days for a list of vacations.
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic calculation of vacation is done!
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
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic calculation of vacation is done!
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
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic calculation of vacation is done!
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
   * !!!!! WARNING: Those methods do have to be changed, as soon as automatic calculation of vacation is done!
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
