package de.maredit.tar.repositories;

import de.maredit.tar.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
  public User findUserByUsername(String username);
}