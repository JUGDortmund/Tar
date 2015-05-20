package de.maredit.tar.controllers;

import de.maredit.tar.properties.VersionProperties;
import de.maredit.tar.providers.VersionProvider;
import de.maredit.tar.services.UserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

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

  @RequestMapping("/overview")
  public String overview(Model model) {
    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("appVersion", versionProvider.getApplicationVersion());
    model.addAttribute("buildnumber", versionProperties.getBuild());

    model.addAttribute("userAccounts", userService.getUserAccountsForYear(
        userService.getSortedUserList(), LocalDate.now().getYear()));


    return "application/overview";
  }
}
