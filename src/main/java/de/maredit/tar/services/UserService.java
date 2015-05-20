package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserAccount;
import de.maredit.tar.models.Vacation;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by czillmann on 19.05.15.
 */
@Service
public interface UserService {

  /**
   * returns a list of users, which is
   * * sorted by lastname
   * * only active users
   *
   * @return the list of users
   */
  public List<User> getSortedUserList();

  /**
   * returns a userAccount, which cumulates all user data for a specific user in a specific year.
   *
   * @param user user of the account data
   * @param year year of the account data
   * @return  the userAccount
   */
  public UserAccount getUserAccountForYear(User user, int year);

  /**
   * returns the userAccount for each user in the list and the specified year
   * @param users the users list
   * @param year the year
   * @return the list of userAccounts
   */
  public List<UserAccount> getUserAccountsForYear(List<User> users, int year);

  /**
   * returns all vacations for a user in the given year. The state is not taken into account.
   * @param user the user
   * @param year the year
   * @return  the list of vacations
   */
  public List<Vacation> getVacationsForUserAndYear(User user, int year);
}
