package de.maredit.tar.controllers;

import de.maredit.tar.models.Vacation;

import de.maredit.tar.models.AccountModel;
import de.maredit.tar.beans.NavigationBean;
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

/**
 * Created by czillmann on 19.05.15.
 */
@Controller
public class OverviewController extends AbstractBaseController {

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
    
    List<AccountModel> models = new ArrayList<>();
    for (UserVacationAccount userVacationAccount : userVacationAccounts) {
      AccountModel accountModel = new AccountModel();
      accountModel.setAccount(userVacationAccount);
      accountModel.setOpenVacationDays(userVacationAccount.getOpenVacationDays());
      accountModel.setTotalVacationDays(userVacationAccount.getTotalVacationDays());
      accountModel.setPreviousYearOpenVacationDays(userVacationAccount.getPreviousYearOpenVacationDays() == null ? 0 : userVacationAccount.getPreviousYearOpenVacationDays());
      List<Vacation> vacations = new ArrayList<>(userVacationAccount.getVacations());
      vacations.sort((v1, v2) -> v1.getCreated().compareTo(v2.getCreated()));
      accountModel.setEntries(vacations);
      models.add(accountModel);
    }

    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("users", allUsers);
    model.addAttribute("filteredUsers", filteredUsers);
    model.addAttribute("accounts", models);

    return "application/overview";
  }
}
