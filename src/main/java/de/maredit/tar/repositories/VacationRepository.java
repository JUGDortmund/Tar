package de.maredit.tar.repositories;

import de.maredit.tar.data.User;
import de.maredit.tar.data.Vacation;
import de.maredit.tar.models.enums.State;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {

  List<Vacation> findVacationByUserOrderByFromAsc(User user);

  List<Vacation> findVacationBySubstituteAndState(User user, State state);

  List<Vacation> findVacationByManagerAndState(User user, State state);

  List<Vacation> findVacationByUserAndStateNotOrderByFromAsc(User user, State state);

  List<Vacation> findVacationBySubstituteAndStateNotOrderByFromAsc(User user, State state);

  List<Vacation> findVacationBySubstituteAndStateNotAndToAfterOrderByFromAsc(User user, State state, LocalDate endDate);

  List<Vacation> findVacationByFromBetweenAndStateInOrToBetweenAndStateIn(
      LocalDate startFrom, LocalDate endFrom, List<State> states, LocalDate startTo,
      LocalDate endTo, List<State> states1);

  List<Vacation> findVacationByUserAndFromBetweenOrUserAndToBetween(
      User user, LocalDate startFrom, LocalDate endFrom, User userAgain, LocalDate startTo,
      LocalDate endTo);
}
