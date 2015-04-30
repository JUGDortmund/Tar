package de.maredit.tar.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;

public interface VacationRepository extends MongoRepository<Vacation, String> {
  public List<Vacation> findVacationByUserOrderByFromAsc(User user);
  public List<Vacation> findVacationBySubstituteAndState(User user, State state);
  public List<Vacation> findVacationByManagerAndState(User user, State state);
  public List<Vacation> findVacationByUserAndStateNotOrderByFromAsc(User user, State state);
  public List<Vacation> findVacationBySubstitute(User user);
}