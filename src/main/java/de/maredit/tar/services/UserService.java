package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserAccount;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by czillmann on 19.05.15.
 */
@Service
public interface UserService {

  public List<User> getSortedUserList();
  public UserAccount getUserAccount(User user);
  public List<UserAccount> getUserAccounts(List<User> users);
}
