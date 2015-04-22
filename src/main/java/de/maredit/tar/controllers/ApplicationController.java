package de.maredit.tar.controllers;

import de.maredit.tar.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

  @Autowired
  private UserRepository userRepository;

  @RequestMapping("/")
  public String index() {
    return "application/index";
  }

  @RequestMapping("/login")
  public String login() {
    return "login";
  }
  
}