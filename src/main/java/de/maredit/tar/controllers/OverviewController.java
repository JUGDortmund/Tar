package de.maredit.tar.controllers;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserAccount;
import de.maredit.tar.properties.VersionProperties;
import de.maredit.tar.providers.VersionProvider;
import de.maredit.tar.repositories.UserRepository;
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
public class OverviewController {

  private static final Logger LOG = LogManager.getLogger(OverviewController.class);

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private VersionProperties versionProperties;

  @Autowired
  private VersionProvider versionProvider;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/overview")
  public String overview(Model model,
                         @RequestParam(value = "employees", required = false) ArrayList<User> filteredUsers) {
    LOG.trace("Filtered users: {}", filteredUsers);
    List<User> allUsers = userService.getSortedUserList();
    List<UserAccount> userAccounts = null;
    if (filteredUsers == null || filteredUsers.isEmpty()) {
      userAccounts = userService.getUserAccountsForYear(
          allUsers, LocalDate.now().getYear());
      filteredUsers = new ArrayList<User>();
    } else {
      userAccounts = userService.getUserAccountsForYear(
          filteredUsers, LocalDate.now().getYear());
    }

    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("appVersion", versionProvider.getApplicationVersion());
    model.addAttribute("buildnumber", versionProperties.getBuild());
    model.addAttribute("users", allUsers);
    model.addAttribute("filteredUsers", filteredUsers);
    model.addAttribute("userAccounts", userAccounts);

    return "application/overview";
  }
}
