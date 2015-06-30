package de.maredit.tar.controllers;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.maredit.tar.beans.NavigationBean;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.VacationRepository;


/**
 * Created by phorninge on 30.06.15.
 */
@Controller
public class AdjustmentController extends AbstractBaseController {

  private static final Logger LOG = LogManager.getLogger(AdjustmentController.class);

  @Autowired
  private VacationRepository vacationRepository;

  @Autowired
  private ApplicationController applicationController;

  @Autowired
  private NavigationBean navigationBean;

  @RequestMapping("/booking")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public String booking(Model model) {
    navigationBean.setActiveComponent(NavigationBean.BOOKING_PAGE);
    List<Vacation> vacations = this.vacationRepository.findAll();
    
    model.addAttribute("vacations", vacations);
    model.addAttribute("loginUser", applicationController.getConnectedUser());
    return "application/booking";
  }
}
