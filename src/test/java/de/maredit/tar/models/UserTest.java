package de.maredit.tar.models;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.maredit.tar.Main;
import de.maredit.tar.repositories.UserRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Main.class)
@ActiveProfiles("test")
public class UserTest {

  UserRepository userRepository = Mockito.mock(UserRepository.class);
  
  private long counter = 0;
  private User user = null;
  private String uidNumber = "jd001";

  @Before
  public void setup() {
    when(userRepository.count()).thenReturn(counter++).thenReturn(counter++);
    user = new User();
    user.setFirstname("John");
    user.setLastname("Deer");
    user.setUidNumber(uidNumber);
    user.setActive(Boolean.TRUE);
    user.setUsername("jdeer");
    when(userRepository.findOne(uidNumber)).thenReturn(user);
  }

  @Test
  public void userModel() {
    long previousCount = userRepository.count();
    userRepository.save(user);
    assertEquals("User count should have increased!", previousCount + 1, userRepository.count());
    
    User user = userRepository.findOne(uidNumber);
    assertEquals("John", user.getFirstname());
    assertEquals("Deer", user.getLastname());
    assertEquals(Boolean.TRUE, user.isActive());
    
    user.setActive(Boolean.FALSE);
    assertEquals(Boolean.FALSE, user.isActive());
  }
}