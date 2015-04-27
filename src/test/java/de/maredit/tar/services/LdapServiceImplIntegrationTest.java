package de.maredit.tar.services;

import com.unboundid.ldap.sdk.SearchResultEntry;

import de.maredit.tar.Main;
import de.maredit.tar.services.configs.LdapConfig;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class LdapServiceImplIntegrationTest {

  @Autowired
  LdapConfig ldapConfig;

  @Autowired
  LdapService ldapService;

  @Test
  public void testGetLdapUserList() throws Exception {
    final List<SearchResultEntry> ldapUserList = ldapService.getLdapUserList();
    assertNotNull("Failed to load user list from LDAP", ldapUserList);
    assertTrue("User list is empty", ldapUserList.size() > 0);
  }

  @Test
  public void testGetLdapTeamleaderList() throws Exception {
    final Set<String> ldapUserList = ldapService.getLdapTeamleaderList();
    assertNotNull("Failed to load team leader list from LDAP", ldapUserList);
    assertTrue("No team leader found", ldapUserList.size() > 0);
  }
}