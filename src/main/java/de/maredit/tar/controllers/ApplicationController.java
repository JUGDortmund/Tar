package de.maredit.tar.controllers;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ApplicationController extends WebMvcConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;


  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model, Vacation vacation) {
    User user = getUser(request);
    vacation.setUser(user);
    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations = this.vacationRepository.findVacationByUserOrderByFromAsc(user);

    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", user);

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
}