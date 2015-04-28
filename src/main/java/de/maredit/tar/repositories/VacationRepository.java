package de.maredit.tar.repositories;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VacationRepository extends MongoRepository<Vacation, String> {
  public List<Vacation> findVacationByUserOrderByFromAsc(User user);
  public List<Vacation> findVacationBySubstitute(User user);
}
