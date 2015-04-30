package de.maredit.tar.tasks;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.unboundid.ldap.sdk.LDAPException;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.services.LdapService;

@Component
public class UserSyncTask {

  private static final Logger LOG = LoggerFactory.getLogger(UserSyncTask.class);

  @Autowired
  private LdapService ldapService;

  @Autowired
  private UserRepository userRepository;

  @Scheduled(cron = "0 */1 * * * ?")
  public void syncLdapUser() {
    try {
      List<User> ldapUserList = ldapService.getLdapUserList();

      List<User> editedUser = new ArrayList<>();
      for (User user : ldapUserList) {
        User localUser = userRepository.findByUidNumber(user.getUidNumber());
        if (localUser == null) {
          localUser = user;
        } else {
          updateUser(localUser, user);
        }
        userRepository.save(localUser);
        editedUser.add(localUser);
      }

      // iterate over all users from repository and delete if they are not part of the LDAP
      deactivateDeletedLdapUser(editedUser);

    } catch (LDAPException e) {
      LOG.error("Failed to sync LDAP users", e);
    }
  }


  private void deactivateDeletedLdapUser(List<User> ldapUsers) {
    List<User> applicationUsers = userRepository.findAll();

    // iterate over all users currently in the application
    for (User applicationUser : applicationUsers) {
      if (!ldapUsers.contains(applicationUser)) {
        applicationUser.setActive(false);
        userRepository.save(applicationUser);
      }
    }
  }

  private void updateUser(User resultEntry, User user) {
    // set name changes here
    user.setMail(resultEntry.getMail());
    user.setUsername(resultEntry.getUidNumber());
    user.setFirstName(resultEntry.getFirstName());
    user.setLastName(resultEntry.getLastName());
    LOG.debug("User updated. username: %s/uidNumber: %s", user.getUsername(), user.getUidNumber());
  }

}
