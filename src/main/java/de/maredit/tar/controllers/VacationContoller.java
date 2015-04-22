package de.maredit.tar.controllers;

import javax.validation.Valid;

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

import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.validators.VacationValidator;
import de.maredit.tar.repositories.VacationRepository;

/**
 * Created by czillmann on 22.04.15.
 */
@Controller
public class VacationContoller extends WebMvcConfigurerAdapter {

  @Autowired
  private VacationRepository vacationRepository;

  private static final Logger log =  LoggerFactory.getLogger(VacationContoller.class);

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(new VacationValidator());
  }

  @RequestMapping(value = "/", method = RequestMethod.POST)
  public String saveVacation(@Valid Vacation vacation, BindingResult bindingResult, Model model) {

    if (bindingResult.hasErrors()) {
      bindingResult.getFieldErrors().forEach(
          fieldError -> log.error(fieldError.getField() + " " + fieldError.getDefaultMessage())
      );
      return "application/index";
    } else {
      this.vacationRepository.save(vacation);

      return "application/index";
    }
  }
}
