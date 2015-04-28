package de.maredit.tar.controllers;

import de.maredit.tar.services.mail.VacationCreateMail;

import de.maredit.tar.models.enums.State;
import org.springframework.security.access.annotation.Secured;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.repositories.VacationRepository;
import de.maredit.tar.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.Valid;

import java.util.List;

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

  @InitBinder("vacation")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
  }

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  public String saveVacation(@Valid Vacation vacation, BindingResult bindingResult, Model model) {
    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> LOG.error(fieldError.getField() + " " + fieldError.getDefaultMessage()));
      User selectedUser = this.userRepository.findByUidNumber(vacation.getUser().getUidNumber());
      List<User> users = this.userRepository.findAll();
      List<Vacation> vacations =
          this.vacationRepository.findVacationByUserOrderByFromAsc(selectedUser);
      model.addAttribute("users", users);
      model.addAttribute("vacations", vacations);
      model.addAttribute("selectedUser", selectedUser);

      return "application/index";
    } else {
      this.vacationRepository.save(vacation);
      this.mailService.sendMail(new VacationCreateMail(vacation));

      return "redirect:/";
    }
  }

  @RequestMapping(value = "/cancelVacation", method = RequestMethod.POST)
  @Secured({"AUTH_OWN_CANCEL_VACATION", "AUTH_CANCEL_VACATION"})
  public String cancelVacation(Vacation vacation, Model model) {

    vacation.setState(State.CANCELED);
    this.vacationRepository.save(vacation);
//    this.mailService.sendMimeMail(vacation);

    User selectedUser = this.userRepository.findByUidNumber(vacation.getUser().getUidNumber());
    List<User> users = this.userRepository.findAll();
    List<Vacation> vacations =
        this.vacationRepository.findVacationByUserOrderByFromAsc(selectedUser);
    model.addAttribute("users", users);
    model.addAttribute("vacations", vacations);
    model.addAttribute("selectedUser", selectedUser);

    return "application/index";
  }

}
