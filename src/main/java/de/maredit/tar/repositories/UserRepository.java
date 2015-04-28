package de.maredit.tar.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import de.maredit.tar.models.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {
  public User findUserByUsername(String username);
  public User findByUidNumber(String uidNumber);

  @Query("{'username' : {$in: ?0}}")
  public List<User> findByUsernames(Set<String> usernames);
}