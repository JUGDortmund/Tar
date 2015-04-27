package de.maredit.tar.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public String index(HttpServletRequest request, Model model) {
      User user = getUser(request);
      
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
        user= this.userRepository.findUserByUsername(String.valueOf(selected));
    }
    
      return user;
    }

    @RequestMapping("calendar")
    public String calendar() {
        return "application/calendar";
    }

}
