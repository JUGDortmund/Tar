package de.maredit.tar.services;

import de.maredit.tar.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class AuthorityMappingServiceTest {

  @Autowired
  private AuthorityMappingService authMappingService;
  
  @Test
  public void getAutorities() {
    Assert.notNull(authMappingService.getGroups(), "No mapping infos loaded");
    Assert.isNull(authMappingService.getGroups().get("unknown"));

    List<String> auths = authMappingService.getGroups().get("tar-user");
    Assert.notNull(auths);
    Assert.notEmpty(auths);
    List<String> assertedAuths = new ArrayList<>();
    assertedAuths.add("ROLE_USER");
    org.junit.Assert.assertEquals(assertedAuths, auths);
    
    List<String> auths2 = authMappingService.getGroups().get("tar-supervisor");
    Assert.notNull(auths2);
    Assert.notEmpty(auths2);
    List<String> assertedAuths2 = new ArrayList<>();
    assertedAuths2.add("ROLE_USER");
    assertedAuths2.add("ROLE_SUPERVISOR");
    org.junit.Assert.assertEquals(assertedAuths2, auths2);
  }
}
