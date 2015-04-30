package de.maredit.tar.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import de.maredit.tar.models.User;

public interface UserRepository extends MongoRepository<User, String> {
  public User findUserByUsername(String username);

  public User findByUidNumber(String uidNumber);

  @Query("{'username' : {$in: ?0}}")
  public List<User> findByUsernames(Set<String> usernames);
}
