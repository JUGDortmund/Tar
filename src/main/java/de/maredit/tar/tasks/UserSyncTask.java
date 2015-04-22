package de.maredit.tar.tasks;

import com.unboundid.ldap.sdk.LDAPException;

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

  @Scheduled(initialDelay = 5000, fixedDelay=5000)
  public void reportCurrentTime() {
    try {
      ldapService.synchronizeLdapUser();
    } catch (LDAPException e) {
      e.printStackTrace();
    }
  }
}