package de.maredit.tar.controllers;

import de.maredit.tar.models.User;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserMangementController {
  
  @RequestMapping("/userDetails")
  public String userDetails(Model model) {
    model.addAttribute("loginUser", new User());
    model.addAttribute("user", new User());
    return "application/userDetails";
  }

}
