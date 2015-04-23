package de.maredit.tar.tasks;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.services.LdapService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserSyncTask {

  private static final Logger LOG = LoggerFactory.getLogger(UserSyncTask.class);

  @Autowired
  UserRepository userRepository;

  @Autowired
  private LdapService ldapService;

  /**
   * Scheduled 5 seconds after start and then every hour
   */
  @Scheduled(initialDelay = 5000, fixedDelay=3600000)
  public void reportCurrentTime() {
    try {
      List<SearchResultEntry> ldapUserList = ldapService.getLdapUserList();
      List<String> editedUser = new ArrayList();
      for (SearchResultEntry userEntry : ldapUserList) {
        if (userEntry != null) {
          User user = userRepository.findByUidNumber(userEntry.getAttributeValue("uidNumber"));
          if (user == null) {
            user = createUser(userEntry);
          } else {
            updateUser(userEntry, user);
          }
          userRepository.save(user);
          editedUser.add(user.getUidNumber());
        }
      }
      dactivateApplicationUser(editedUser);

    } catch (LDAPException e) {
      e.printStackTrace();
    }
  }

  private void dactivateApplicationUser(List<String> editedUser) {
    List<User> applicationUsers = userRepository.findAll();

    //iterate over all users currently in the application
    for (User applicationUser : applicationUsers) {
      if (!editedUser.contains(applicationUser.getUidNumber())) {
        applicationUser.setActive(false);
        userRepository.save(applicationUser);
      }
    }
  }

  private void updateUser(SearchResultEntry resultEntry, User user) {
    // set name changes here
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    LOG.debug("User updated. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
  }

  private User createUser(SearchResultEntry resultEntry) {
    User user;
    user = new User();
    user.setMail(resultEntry.getAttributeValue("mail"));
    user.setUidNumber(resultEntry.getAttributeValue("uidNumber"));
    user.setUsername(resultEntry.getAttributeValue("uid"));
    user.setFirstName(resultEntry.getAttributeValue("cn"));
    user.setLastName(resultEntry.getAttributeValue("sn"));
    user.setActive(Boolean.TRUE);

    LOG.debug("User created. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
    return user;
  }
}