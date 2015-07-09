package de.maredit.tar.services;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.data.ManualEntry;
import de.maredit.tar.data.User;
import de.maredit.tar.data.UserVacationAccount;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.properties.VacationProperties;
import de.maredit.tar.repositories.ManualEntryRepository;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.UserVacationAccountRepository;
import de.maredit.tar.repositories.VacationRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by czillmann on 19.05.15.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final Logger LOG = LogManager.getLogger(UserServiceImpl.class);

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserVacationAccountRepository userVacationAccountRepository;

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private ManualEntryRepository manualEntryRepository;

  @Autowired
  private LdapService ldapService;

  @Autowired
  private VacationService vacationService;

  @Autowired
  private VacationProperties vacationProperties;

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
  public List<User> getManagerList() {
    List<User> managerList = new ArrayList<User>();
    try {
      managerList = userRepository.findByUsernames(ldapService.getLdapSupervisorList());
      managerList =
          managerList.stream().filter(e -> e.isActive())
              .sorted((e1, e2) -> e1.getLastname().compareTo(e2.getLastname()))
              .collect(Collectors.toList());

    } catch (LDAPException e) {
      LOG.error("Error while reading manager list for vacation form", e);
    }
    return managerList;
  }

  @Override
  public UserVacationAccount getUserVacationAccountForYear(User user, int year) {
    UserVacationAccount vacationAccount =
        userVacationAccountRepository.findUserVacationAccountByUserAndYear(user, year);
    if (vacationAccount == null) {
      vacationAccount = new UserVacationAccount();
      vacationAccount.setUser(user);
      vacationAccount.setYear(year);
      vacationAccount.setTotalVacationDays(user.getVacationDays() == null ? vacationProperties
          .getDefaultVacationDays() : user.getVacationDays());
      vacationAccount.setExpiryDate(LocalDate.parse(year + "-" + vacationProperties.getExpiryDate(), DATE_PATTERN));
    }
    UserVacationAccount previousVacationAccount =
        userVacationAccountRepository.findUserVacationAccountByUserAndYear(user, year - 1);
    if (previousVacationAccount != null) {
      vacationAccount.setPreviousYearOpenVacationDays(vacationService.getRemainingVacationEntitlement(previousVacationAccount).getDays());
    }
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
    LocalDate startOfYear =
        LocalDate.now().withYear(year).with(TemporalAdjusters.firstDayOfYear()).minusDays(1);
    LocalDate endOfYear =
        LocalDate.now().withYear(year).with(TemporalAdjusters.lastDayOfYear()).plusDays(1);

    return this.vacationRepository.findVacationByUserAndFromBetweenOrUserAndToBetween(user,
        startOfYear, endOfYear, user, startOfYear, endOfYear);
  }

  @Override
  public void addVacationForUserAndYear(Vacation vacation, User user, int year){
    UserVacationAccount userVacationAccount = getUserVacationAccountForYear(user, year);
    userVacationAccount.addVacation(vacation);

    vacationRepository.save(vacation);
    userVacationAccountRepository.save(userVacationAccount);
  }

  @Override
  public void addManualEntryToVacationAccout(ManualEntry manualEntry, UserVacationAccount userVacationAccount) {
    userVacationAccount.addManualEntry(manualEntry);
    manualEntryRepository.save(manualEntry);
    userVacationAccountRepository.save(userVacationAccount);

    Vacation referencedVacation = manualEntry.getVacation();
    if(referencedVacation != null){
      referencedVacation.setManualEntry(manualEntry);
      vacationRepository.save(referencedVacation);
    }
  }
}