package de.maredit.tar.repositories;

import de.maredit.tar.models.enums.State;

import org.springframework.data.web.SortDefault;
import org.springframework.data.mongodb.repository.Query;
import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {
  
  public List<Vacation> findVacationByUserAndStateNotOrderByFromAsc(User user, State state);
  public List<Vacation> findVacationBySubstitute(User user);
}
