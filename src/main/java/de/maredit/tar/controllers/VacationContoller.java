package de.maredit.tar.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.VacationRepository;

/**
 * Created by czillmann on 22.04.15.
 */
@Controller
public class VacationContoller {
  @Autowired
  private VacationRepository vacationRepository;

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @RequestMapping(value = "/saveVacation", method = RequestMethod.POST)
  public String saveVacation(@Valid Vacation vacation, BindingResult bindingResult, Model model) {

    if (bindingResult.hasErrors()) {
      model.addAttribute("vacation", vacation);
      model.addAttribute("error", true);
      model.addAttribute("fields", bindingResult);
      bindingResult.getFieldErrors().forEach(
          fieldError -> log.error(fieldError.getDefaultMessage())
      );

      return "application/index";
    } else {
      this.vacationRepository.save(vacation);

      return "application/index";
    }
  }
}
