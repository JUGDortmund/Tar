package de.maredit.tar.repositories;

import de.maredit.tar.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Set;

public interface UserRepository extends MongoRepository<User, String> {
  User findUserByUsername(String username);

  User findByUidNumber(String uidNumber);

  @Query("{'username' : {$in: ?0}}")
  List<User> findByUsernames(Set<String> usernames);
}
