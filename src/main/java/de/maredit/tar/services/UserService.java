package de.maredit.tar.services;

import de.maredit.tar.models.User;
import de.maredit.tar.models.UserVacationAccount;
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
   * <li>sorted by lastname</li>
   * <li>only active users</li>
   *
   * @return the list of users
   */
  public List<User> getSortedUserList();

  /**
   * returns a list of users, which are managers and sorted by lastname
   *
   * @return the list of managers
   */
  public List<User> getManagerList();

  /**
   * returns a userVacationAccount, which cumulates all user data for a specific user in a specific
   * year.
   *
   * @param user user of the account data
   * @param year year of the account data
   * @return the userVacationAccount
   */
  public UserVacationAccount getUserVacationAccountForYear(User user, int year);

  /**
   * returns the userVacationAccounts for each user in the list and the specified year
   *
   * @param users the users list
   * @param year  the year
   * @return the list of userVacationAccounts
   */
  public List<UserVacationAccount> getUserVacationAccountsForYear(List<User> users, int year);

  /**
   * returns all vacations for a user in the given year. The state is not taken into account.
   *
   * @param user the user
   * @param year the year
   * @return the list of vacations
   */
  public List<Vacation> getVacationsForUserAndYear(User user, int year);
}
