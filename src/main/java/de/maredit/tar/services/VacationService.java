package de.maredit.tar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.maredit.tar.models.Vacation;
import de.maredit.tar.repositories.VacationRepository;

@Service
public class VacationService {
  
  @Autowired
  private VacationRepository vacationRepository;

  public List<Vacation> findVacationByUser() {
    return null;
  }
}