package de.maredit.tar.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ApplicationController {

  @SuppressWarnings("unused")
  private static final Logger LOG = LogManager.getLogger(ApplicationController.class);

  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("/overview")
  public String overview(Model model) {
    model.addAttribute("something", 94);
    return "application/overview";
  }
}
