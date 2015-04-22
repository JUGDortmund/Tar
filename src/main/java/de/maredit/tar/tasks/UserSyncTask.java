package de.maredit.tar.tasks;

import de.maredit.tar.services.LdapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UserSyncTask {

  @Autowired
  private LdapService ldapService;

  @Scheduled(fixedDelay=5000)
  public void reportCurrentTime() {
    ldapService.synchronizeLdapUser();
  }
}