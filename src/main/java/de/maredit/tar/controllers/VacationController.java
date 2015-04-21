package de.maredit.tar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import de.maredit.tar.services.VacationService;

@Controller
public class VacationController {
  
  @Autowired
  private VacationService vacationService;
  
}