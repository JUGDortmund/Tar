package de.maredit.tar.controllers;

import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import de.maredit.tar.repositories.UserRepository;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import de.maredit.tar.models.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserMangementController extends WebMvcConfigurerAdapter {
  
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private ApplicationController applicationController;
  
  @InitBinder
  private void initBinder(WebDataBinder binder) {
    binder.registerCustomEditor(User.class, new PropertyEditorSupport() {
      
      @Override
      public String getAsText() {
        if (getValue() == null) {
          return "";
        }
        return ((User)getValue()).getId();
      }
      
      @Override
      public void setAsText(String text) throws IllegalArgumentException {
        if (StringUtils.isNotBlank(text)) {
          setValue(userRepository.findOne(text));
        }
      }
    });

    binder.setAllowedFields("vacationDays");
  }
  
  @ModelAttribute("user")
  public User getUser(@RequestParam(value = "id", required = false) String id) {
    if (StringUtils.isBlank(id)) {
      return null;
    }
    return userRepository.findOne(id);
  }

  
  @RequestMapping("/userDetails")
  public String userDetails(Model model, @RequestParam("user") User user) {
    model.addAttribute("loginUser", applicationController.getConnectedUser());
    model.addAttribute("user", user);
    return "application/userDetails";
  }

  @RequestMapping(value="/updateDetails", method= RequestMethod.POST)
  public String updateUserDetails(@ModelAttribute("user") User user, Model model, BindingResult bindingResult) {
    userRepository.save(user);
    return "redirect:/overview";
  }
}
