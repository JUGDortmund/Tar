package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import de.maredit.tar.models.User;

public interface UserRepository extends MongoRepository<User, String> {
  public User findUserByUsername(String username);
  public User findByUidNumber(String uidNumber);
}