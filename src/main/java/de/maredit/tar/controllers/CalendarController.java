package de.maredit.tar.controllers;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.VacationRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by czillmann on 29.04.15.
 */

@Controller
public class CalendarController {

  private static final Logger LOG = LogManager.getLogger(CalendarController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @RequestMapping("/calendar")
  public String calendar(Model model) {
    List<Vacation> vacations =  this.vacationRepository.findAll();
    model.addAttribute("vacations", vacations);

    return "application/calendar";
  }
}
