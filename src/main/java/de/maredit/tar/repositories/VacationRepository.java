package de.maredit.tar.repositories;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {

  public List<Vacation> findVacationByUserOrderByFromAsc(User user);

  public List<Vacation> findVacationByFromBetweenOrToBetween(LocalDate startFrom, LocalDate endFrom, LocalDate startTo, LocalDate endTo);
}
