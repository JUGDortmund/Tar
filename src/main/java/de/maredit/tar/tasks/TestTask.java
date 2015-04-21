package de.maredit.tar.tasks;

import de.maredit.tar.services.LdapService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestTask {

  @Autowired
  private LdapService ldapService;

  @Scheduled(fixedRate = 50000)
  public void reportCurrentTime() {
    ldapService.synchronizeLdapUser();
  }
}