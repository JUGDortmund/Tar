package de.maredit.tar.repositories;

import de.maredit.tar.data.UserVacationAccount;

import de.maredit.tar.data.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVacationAccountRepository extends MongoRepository<UserVacationAccount, String> {
  UserVacationAccount findUserVacationAccountByUserAndYear(User user, Integer year);
}
