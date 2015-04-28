package de.maredit.tar.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.LdapService;
import de.maredit.tar.services.MailService;

/**
 * Created by czillmann on 22.04.15.
 */
@Controller
public class VacationContoller extends WebMvcConfigurerAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(VacationContoller.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailService mailService;

  @Autowired
  private LdapService ldapService;

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
  }

  @RequestMapping("/")
  public String index(HttpServletRequest request, Model model, Vacation vacation) {
    User user = getUser(request);
    vacation.setUser(user);
    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations = this.vacationRepository.findVacationByUserOrderByFromAsc(user);
    List<User> managerList = getManagerList();
    List<Vacation> substitutes = this.vacationRepository.findVacationBySubstitute(getConnectedUser());
    
    setVacationFormModelValues(model, user, users, vacations, managerList, substitutes);
    return "application/index";
  }
  
  @RequestMapping("/vacation")
  public String vacation(@RequestParam(value="id") String id, Model model) {
    Vacation vacation = this.vacationRepository.findOne(id);
    model.addAttribute("vacation", vacation);
    
    return "application/vacation";
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  public String saveVacation(@Valid Vacation vacation, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getField() + " " + fieldError.getDefaultMessage()));
      User selectedUser = this.userRepository.findByUidNumber(vacation.getUser().getUidNumber());
      List<User> users = this.userRepository.findAll();
      List<Vacation> vacations = this.vacationRepository.findVacationByUserOrderByFromAsc(selectedUser);
      List<User> managerList = getManagerList();
      List<Vacation> substitutes = this.vacationRepository.findVacationBySubstitute(getConnectedUser());
      
      setVacationFormModelValues(model, selectedUser, users, vacations, managerList, substitutes);
      return "application/index";
    } else {
      this.vacationRepository.save(vacation);
      this.mailService.sendMimeMail(vacation);

      return "redirect:/";
    }
  }

  private void setVacationFormModelValues(Model model, User selectedUser, List<User> users,
                                          List<Vacation> vacations, List<User> managerList, List<Vacation> substitutes) {
    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", selectedUser);
    model.addAttribute("managers", managerList);
    model.addAttribute("substitutes", substitutes);
  }

  private List<User> getManagerList() {
    List<User> managerList = new ArrayList<User>();
    try {
      managerList =
          userRepository.findByUsernames(ldapService.getLdapManagerList());
      List<User> filteredManagerList =
          managerList.stream().filter(e -> e.isActive()).collect(Collectors.toList());

    } catch (LDAPException e) {
      LOG.error("Error while reading manager list for vacation form", e);
    }
    return managerList;
  }
  
  private User getConnectedUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User user = this.userRepository.findUserByUsername(auth.getName());
    
    return user;
  }

  private User getUser(HttpServletRequest request) {
    User user = null;
    Object selected = request.getParameter("selected");
    if (selected == null) {
      user = getConnectedUser();
    } else {
      user = this.userRepository.findUserByUsername(String.valueOf(selected));
    }
    return user;
  }
}
