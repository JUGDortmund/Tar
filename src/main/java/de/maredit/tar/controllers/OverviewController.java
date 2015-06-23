package de.maredit.tar.controllers;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.models.AccountModel;
import de.maredit.tar.models.User;
import de.maredit.tar.models.UserVacationAccount;
import de.maredit.tar.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by czillmann on 19.05.15.
 */
@Controller
public class OverviewController {

  private static final Logger LOG = LogManager.getLogger(OverviewController.class);

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private UserService userService;

  @Autowired
  private NavigationBean navigationBean;

  @RequestMapping("/overview")
  public String overview(Model model,
                         @RequestParam(value = "employees", required = false) ArrayList<User> filteredUsers) {
    navigationBean.setActiveComponent(NavigationBean.OVERVIEW_PAGE);
    LOG.trace("Filtered users: {}", filteredUsers);
    List<User> allUsers = userService.getSortedUserList();
    List<UserVacationAccount> userVacationAccounts = null;
    if (filteredUsers == null || filteredUsers.isEmpty()) {
      userVacationAccounts = userService.getUserVacationAccountsForYear(
          allUsers, LocalDate.now().getYear());
      filteredUsers = new ArrayList<User>();
    } else {
      userVacationAccounts = userService.getUserVacationAccountsForYear(
          filteredUsers, LocalDate.now().getYear());
    }

    List<AccountModel> accounts = new ArrayList<>();
    for (UserVacationAccount userVacationAccount : userVacationAccounts) {
      AccountModel accountModel = new AccountModel();
      accountModel.setId(userVacationAccount.getId());
      accountModel.setUser(userVacationAccount.getUser());
      accountModel.setTotalVacationDays(userVacationAccount.getTotalVacationDays());
      accountModel.setPreviousYearOpenVacationDays(userVacationAccount.getPreviousYearOpenVacationDays());
      accountModel.setApprovedVacationDays(getApprovedVacationDays(userVacationAccount.getVacations()));
      accountModel.setPendingVacationDays(getPendingVacationDays(userVacationAccount.getVacations()));
      accounts.add(accountModel);
    }
    
    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("users", allUsers);
    model.addAttribute("filteredUsers", filteredUsers);
    model.addAttribute("accounts", accounts);

    return "application/overview";
  }
  
  /**
   * Helper method to retrieve the amount of approved vacation days for a list of vacations.
   *
   * @param set the set to analyze
   * @return the amount of approved vacation days
   */
  private double getApprovedVacationDays(Set<Vacation> set) {
    return set != null ? set.stream().filter(vacation -> vacation.getState() == State.APPROVED)
        .mapToDouble(vacation -> vacation.getDays()).sum() : 0;
  }

  /**
   * Helper method to retrieve the amount of pending vacation days (which are already planned but
   * not accepted yet) for a list of vacations.
   *
   * @param set the set to analyze
   * @return the amount of pending vacation days
   */
  private double getPendingVacationDays(Set<Vacation> set) {
    return set != null ? set
        .stream()
        .filter(
            vacation -> vacation.getState() == State.REQUESTED_SUBSTITUTE
                || vacation.getState() == State.WAITING_FOR_APPROVEMENT)
        .mapToDouble(vacation -> vacation.getDays()).sum() :0;
  }

}
