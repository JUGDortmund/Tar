package de.maredit.tar.controllers;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;

@Controller
public class ApplicationController {

  @Autowired
  SessionLocaleResolver localeResolver;

  @Autowired
  private UserRepository userRepository;

  @SuppressWarnings("unused")
  private static final Logger LOG = LogManager.getLogger(ApplicationController.class);

  @RequestMapping("/login")
  public String login() throws Exception {
    return "login";
  }

  public User getConnectedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (!auth.isAuthenticated()) {
      return null;
    }
    User user = this.userRepository.findUserByUsername(auth.getName());

    return user;

  }

  @RequestMapping(value = "/changeLanguage", method = RequestMethod.POST)
  public String changeLanguage(HttpServletRequest request,
      @RequestParam(value = "language") String id, Model model) {
    Locale newLocale = new Locale(id);
    localeResolver.setDefaultLocale(newLocale);
    LOG.debug(request.getContextPath());
    return "redirect:/";
  }
}
