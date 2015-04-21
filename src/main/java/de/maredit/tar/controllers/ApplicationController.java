package de.maredit.tar.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.services.UserService;
import de.maredit.tar.services.VacationService;

@Controller
public class ApplicationController {
    
    @Autowired
    private VacationService vacationService;
    
    @Autowired
    private UserService userService;
    
    @RequestMapping("")
    public String index(Model model) {
      List<User> users = userService.findAllUsers();
      List<Vacation> vacations = vacationService.findVacationByUser();
      
      model.addAttribute("users", users);
      model.addAttribute("vacations", vacations);
      
      return "application/index";
    }
    
    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}