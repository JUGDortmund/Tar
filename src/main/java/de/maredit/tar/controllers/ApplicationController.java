package de.maredit.tar.controllers;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ApplicationController {

  private static final Logger LOG = LogManager.getLogger(ApplicationController.class);

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("calendar")
  public String calendar() {
    return "application/calendar";
  }
}
