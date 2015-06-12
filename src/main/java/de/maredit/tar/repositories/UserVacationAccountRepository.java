package de.maredit.tar.repositories;

import de.maredit.tar.models.UserVacationAccount;

import de.maredit.tar.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserVacationAccountRepository extends MongoRepository<UserVacationAccount, String> {
  UserVacationAccount findUserVacationAccountByUserAndYear(User user, Integer year);
}
