package de.maredit.tar.repositories;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {
  public List<Vacation> findVacationByUserOrderByFromAsc(User user);
  public List<Vacation> findVacationBySubstituteAndState(User user, State state);
  public List<Vacation> findVacationByManagerAndState(User user, State state);
}
