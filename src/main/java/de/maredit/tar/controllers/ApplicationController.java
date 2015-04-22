package de.maredit.tar.controllers;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Controller
public class ApplicationController extends WebMvcConfigurerAdapter {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private VacationRepository vacationRepository;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String index(Model model, Vacation vacation) {
    return "application/index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }
}
