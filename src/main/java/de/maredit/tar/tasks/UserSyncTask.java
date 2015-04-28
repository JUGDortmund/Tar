package de.maredit.tar.tasks;

import de.maredit.tar.repositories.UserRepository;
import de.maredit.tar.models.User;

import java.util.ArrayList;
import java.util.List;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResultEntry;

import de.maredit.tar.services.LdapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

      deactivateUserNotInLdap(ldapUserList);

    } catch (LDAPException e) {
      LOG.error("Failed to sync LDAP users", e);
    }
  }


  private void deactivateUserNotInLdap(List<User> ldapUsers) {
    List<User> applicationUsers = userRepository.findAll();

    // iterate over all users currently in the application
    for (User applicationUser : applicationUsers) {
      if (!ldapUsers.contains(applicationUser.getUidNumber())) {
        applicationUser.setActive(false);
        userRepository.save(applicationUser);
      }
    }
  }

}
