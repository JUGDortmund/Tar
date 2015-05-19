package de.maredit.tar.repositories;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import de.maredit.tar.models.enums.State;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {

  public List<Vacation> findVacationByUserOrderByFromAsc(User user);

  public List<Vacation> findVacationByUserAndCreatedBetween(User user, LocalDate startBetween,
                                                            LocalDate endBetween);

  public List<Vacation> findVacationBySubstituteAndState(User user, State state);
  
  public List<Vacation> findVacationByManagerAndState(User user, State state);

  public List<Vacation> findVacationByUserAndStateNotOrderByFromAsc(User user, State state);

  public List<Vacation> findVacationBySubstituteAndStateNotOrderByFromAsc(User user, State state);
  
  public List<Vacation> findVacationByFromBetweenAndStateInOrToBetweenAndStateIn(
      LocalDate startFrom, LocalDate endFrom, List<State> states, LocalDate startTo,
      LocalDate endTo, List<State> states1);
}
