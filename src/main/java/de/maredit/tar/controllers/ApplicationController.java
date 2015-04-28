package de.maredit.tar.controllers;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@Controller
public class ApplicationController {

  private static final Logger LOG = LogManager.getLogger(ApplicationController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private LdapService ldapService;


  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model, Vacation vacation) {
    User user = getUser(request);
    vacation.setUser(user);
    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations = this.vacationRepository.findVacationByUserOrderByFromAsc(user);

    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", user);
    try {
      model.addAttribute("managers", userRepository.findByUsernames(ldapService.getLdapManagerList()));
    } catch (LDAPException e) {
      LOG.error("Error while reading manager list for vacation form", e);
    }

    return "application/index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  private User getUser(HttpServletRequest request) {
    User user = null;
    Object selected = request.getParameter("selected");
    if (selected == null) {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      user = this.userRepository.findUserByUsername(auth.getName());
    } else {
      user = this.userRepository.findUserByUsername(String.valueOf(selected));
    }
    return user;
  }

  @RequestMapping("calendar")
  public String calendar() {
    return "application/calendar";
  }
}
