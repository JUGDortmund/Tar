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

  public UserAccount getUserAccount(User user) {
    UserAccount account = new UserAccount();
    List<Vacation> vacations =
        this.vacationRepository.findVacationByUserAndCreatedBetween(
            user,
            LocalDate
                .now()
                .with(
                    TemporalAdjusters
                        .firstDayOfYear()),
            LocalDate
                .now()
                .with(
                    TemporalAdjusters
                        .lastDayOfYear()));

    double
        approvedVacationDays =
        vacations.stream().filter(vacation -> vacation.getState() == State.APPROVED)
            .mapToDouble(vacation -> vacation.getDays()).sum();

    double
        pendingVacationDays =
        vacations.stream().filter(vacation -> vacation.getState() == State.REQUESTED_SUBSTITUTE
                                              || vacation.getState()
                                                 == State.WAITING_FOR_APPROVEMENT)
            .mapToDouble(vacation -> vacation.getDays()).sum();

    double openVacationDays =
        vacations.stream()
            .filter(vacation -> vacation.getCreated().getYear() == LocalDate.now().getYear())
            .max((v1, v2) -> v1.getCreated().compareTo(v2.getCreated())).get().getDaysLeft();
    account.setUser(user);
    account.setVacations(vacations);
    account.setApprovedVacationDays(approvedVacationDays);
    account.setPendingVacationDays(pendingVacationDays);
    account.setOpenVacationDays(openVacationDays);

    return account;
  }

  public List<UserAccount> getUserAccounts(List<User> users){
    List<UserAccount> accounts = new ArrayList<UserAccount>();
    for (User user: users){
      accounts.add(getUserAccount(user));
    }
    return accounts;
  }
}
