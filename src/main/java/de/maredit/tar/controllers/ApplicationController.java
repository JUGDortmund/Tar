package de.maredit.tar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;

@Controller
public class ApplicationController {
    
    @Autowired
    private VacationRepository vacationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @RequestMapping("/")
    public String index(Model model) {
      List<User> users = this.userRepository.findAll();
      List<Vacation> vacations = vacationRepository.findVacationByUser(user);
      
      model.addAttribute("users", users);
      model.addAttribute("vacations", vacations);
      
      return "application/index";
    }
    
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}