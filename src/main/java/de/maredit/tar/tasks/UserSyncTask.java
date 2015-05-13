package de.maredit.tar.tasks;

import org.apache.commons.lang3.StringUtils;

import de.maredit.tar.models.User;
import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.services.LdapService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.unboundid.ldap.sdk.LDAPException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserSyncTask {

  private static final Logger LOG = LogManager.getLogger(UserSyncTask.class);

  @Autowired
  private LdapService ldapService;

  @Autowired
  private UserRepository userRepository;

  @Scheduled(cron = "0 0 */1 * * ?")
  public void syncLdapUser() {
    try {
      LOG.debug("Syncing users");
      List<User> ldapUserList = ldapService.getLdapUserList();

      List<User> editedUser = new ArrayList<>();
      for (User user : ldapUserList) {
        User localUser = userRepository.findByUidNumber(user.getUidNumber());
        if (localUser == null) {
          localUser = user;
          LOG.debug("User created. username: {} / uidNumber: {}", user.getUsername(),
              user.getUidNumber());
        } else {
          updateUser(localUser, user);
          LOG.debug("User updated. username: {} / uidNumber: {}", user.getUsername(),
              user.getUidNumber());
        }
        userRepository.save(localUser);
        editedUser.add(localUser);
      }

      // iterate over all users from repository and delete if they are not
      // part of the LDAP
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
    user.setUsername(resultEntry.getUsername());
    user.setFirstname(resultEntry.getFirstname());
    user.setLastname(resultEntry.getLastname());
    try {
      user.setPhoto(Optional.ofNullable(resultEntry.getPhoto()).orElse(StringUtils.EMPTY).getBytes("UTF8"));
    } catch (UnsupportedEncodingException e) {
      LOG.error("Failed to sync utf-8 user photo", e);
    }
  }
}
