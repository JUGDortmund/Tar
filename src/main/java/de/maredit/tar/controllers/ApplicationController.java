package de.maredit.tar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;

@Controller
public class ApplicationController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private VacationRepository vacationRepository;

  @RequestMapping("/")
  public String index(Model model) {
    Vacation vacation = new Vacation();
    model.addAttribute("vacation", vacation);

    return "application/index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }
}
