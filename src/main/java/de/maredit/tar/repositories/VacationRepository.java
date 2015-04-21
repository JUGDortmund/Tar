package de.maredit.tar.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.User;
import de.maredit.tar.models.Vacation;

public interface VacationRepository extends MongoRepository<Vacation, String> {
  public List<Vacation> findVacationByUser(User user);
}